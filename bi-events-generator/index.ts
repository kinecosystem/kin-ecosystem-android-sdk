#!/usr/bin/env node

import { readdirSync, unlinkSync, existsSync, readFileSync, writeFileSync } from "fs";
import { exec } from"child_process";

const SOURCE_PATH = "../../kin-bi/json_schemas";
const TMP_PATH = "./java-gen-tmp";
const TARGET_PATH = "../kin-ecosystem-core/src/main/java";
const PACKAGE_PATH = "kin/ecosystem/core/bi/events";
const AUGEMENTED_BY_SCRIPT_COMMENT = "// Augmented by script\n";

/**
 * Executes a command line command and returns a promise to success/fail
 *
 * @param {string} cmd
 * @returns {Promise<void>}
 */
async function runCommand(cmd: string) {
    return new Promise((resolve, reject) => {
        exec(cmd, (err) => {
            if (err) {
                reject(err);

            } else {
                resolve();
            }
        });
    });
}

/**
 * Prepares the environment for the source file generation
 *
 * @returns {Promise<Promise<void>>}
 */
async function prepare() {
    const command = `rm -f ${ TARGET_PATH }/${ PACKAGE_PATH }/*`;
    return runCommand(command);
}

/**
 * Generates the source files
 *
 * @returns {Promise<Promise<void>>}
 */
async function generate() {
    const command = `jsonschema2pojo -a GSON -c -E -S -R -ds -s ${ SOURCE_PATH } -t ${ TMP_PATH } -p kin.ecosystem.core.bi.events`;
    return runCommand(command);
}

class Argument {
    public readonly type: string;
    public readonly name: string;

    constructor(type: string, name: string) {
        this.type = type;
        this.name = name;
    }

    toString(): string {
        return `${ this.type } ${ this.name }`;
    }
}

class EventConstructor {
    private static readonly ARGS_REG_EX = /\s*,\s*/g;
    private static readonly AUGMENTED_ARGS = ["eventName", "common", "user"];

    private readonly className: string;
    private readonly args: Argument[];

    constructor(className: string, args: string) {
        this.className = className;
        this.args = args
            .split(EventConstructor.ARGS_REG_EX)
            .map(arg => arg.split(/\s+/))
            .map(arg => new Argument(arg[0], arg[1]));

        // needed because it is passed as a callback
        this.superCallValueFor = this.superCallValueFor.bind(this);
    }

    public createFactory(): string {
        let content = EventConstructor.indent(1) + AUGEMENTED_BY_SCRIPT_COMMENT;
        content += `${ EventConstructor.indent(1) }public static ${ this.className } create(`;

        content += this.args
            .filter(arg => !EventConstructor.AUGMENTED_ARGS.includes(arg.name))
            .join(", ");

        content += ") {\n";
        content += `${ EventConstructor.indent(2) }return new ${ this.className }(`;

        content += this.args.map(this.superCallValueFor).join(", ") + ");\n";

        content += `${ EventConstructor.indent(1) }}\n`;

        return content;
    }

    private superCallValueFor(arg: Argument) {
        switch (arg.name) {
            case "eventName":
                return `EventName.${ EventConstructor.toSnakeCase(this.className).toUpperCase() }`;

            case "common":
                return "Store.common";

            case "user":
                return "Store.user";

            default:
                return arg.name;
        }
    }

    private static indent(level: 1 | 2 | 3): string {
        let content = "";
        while (content.length < level * 4) {
            content += "    ";
        }
        return content;
    }

    private static toSnakeCase(str: string): string {
        const upperChars = str.match(/([A-Z])/g);
        if (!upperChars) {
            return str;
        }

        let copy = str.toString();
        for (let i = 0, n = upperChars.length; i < n; i++) {
            copy = copy.replace(new RegExp(upperChars[i]), "_" + upperChars[i].toLowerCase());
        }

        if (copy.slice(0, 1) === "_") {
            copy = copy.slice(1);
        }

        return copy;
    }
}

/**
 * Modifies a source file after it was generated
 *
 * @param path for the generated source file
 * @returns {string} the new content for the file
 */
function processSourceFile(path: string): string {
    let content = readFileSync(path, "utf8");
    const className = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

    if (["User", "Common"].includes(className)) {
        return content;
    }

    content = content.replace(/(import [^;]+;)/, AUGEMENTED_BY_SCRIPT_COMMENT + "import kin.ecosystem.core.bi.Store;\n\n$1")

    const ctorRegEx = new RegExp("public " + className + "\\((.+)\\)", "g");
    const ctors = [] as EventConstructor[];

    let match: RegExpExecArray | null = null;
    while ((match = ctorRegEx.exec(content)) !== null) {
        ctors.push(new EventConstructor(className, match[1]));
    }

    const classDef = `public class ${ className } {\n`;
    return content.replace(classDef, classDef + ctors.map(ctor => ctor.createFactory()).join("\n"));
}

/**
 * Processes the source files after they were generated to add our own flavour.
 *
 * @returns {Promise<void>}
 */
async function postProcess() {
    const files = readdirSync(`${ TMP_PATH }/${ PACKAGE_PATH }`);
    files.forEach(file => {
        file = `${ TMP_PATH }/${ PACKAGE_PATH }/${ file }`;

        if (file.endsWith("_.java")) {
            unlinkSync(file);
        } else {
            writeFileSync(file, processSourceFile(file), "utf8");
        }
    });
}

/**
 * Moves the final source files to the target directory
 *
 * @returns {Promise<Promise<void>>}
 */
async function moveFiles() {
    const command = `mv ${ TMP_PATH }/${ PACKAGE_PATH }/* ${ TARGET_PATH }/${ PACKAGE_PATH }`;
    return runCommand(command);
}

/**
 * Cleans up after all is done, such as removing temp files/dirs
 *
 * @returns {Promise<Promise<void>>}
 */
async function cleanup() {
    const command = `rm -rf ${ TMP_PATH }`;
    return runCommand(command);
}

async function main() {
    try {
        await prepare();
    } catch (e) {
        console.error("failed to prepare, error: ", e);
        process.exit(1);
    }

    try {
        await generate();
    } catch (e) {
        console.error("failed to generate source files, error: ", e);
        process.exit(1);
    }

    try {
        await postProcess();
    } catch (e) {
        console.error("failed to post process source files, error: ", e);
        process.exit(1);
    }

    try {
        await moveFiles();
    } catch (e) {
        console.error("failed to move source files, error: ", e);
        process.exit(1);
    }

    try {
        await cleanup();
    } catch (e) {
        console.error("failed to clean up, error: ", e);
        process.exit(1);
    }
}

main().then(() => console.log("Finished generating source files.\nisn't nitzan awesome?"));
