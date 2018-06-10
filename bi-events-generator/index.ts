#!/usr/bin/env node

import { readdirSync, unlinkSync, existsSync, readFileSync, writeFileSync } from "fs";
import { exec } from"child_process";

const SOURCE_PATH = "../../kin-bi/json_schemas";
const TMP_PATH = "./java-gen-tmp";
const TARGET_PATH = "../kin-ecosystem-sdk/src/main/java";
const PACKAGE_PATH = `com/kin/ecosystem/bi/events`;
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
    const path = `${ TARGET_PATH }/${ PACKAGE_PATH }`;
    let command: string;

    if (!existsSync(path)) {
        command = `mkdir ${ path }`;
    } else {
        command = `rm -f ${ path }/*`;
    }

    return runCommand(command);
}

/**
 * Generates the source files
 *
 * @returns {Promise<Promise<void>>}
 */
async function generate() {
    const command = `jsonschema2pojo -a GSON -c -E -S -R -ds -s ${ SOURCE_PATH } -t ${ TMP_PATH } -p com.kin.ecosystem.bi.events`;
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
        content += `${ EventConstructor.indent(2) }return new ${ this.className }(\n`;

        content += this.args.map(arg => EventConstructor.indent(3) + this.superCallValueFor(arg)).join(",\n") + ");\n\n";

        content += `${ EventConstructor.indent(1) }}\n`;

        return content;
    }

    public createSender(): string {
        let content = EventConstructor.indent(1) + AUGEMENTED_BY_SCRIPT_COMMENT;
        content += `${ EventConstructor.indent(1) }public static void fire(`;

        content += this.args
            .filter(arg => !EventConstructor.AUGMENTED_ARGS.includes(arg.name))
            .join(", ");

        content += ") {\n";
        content += `${ EventConstructor.indent(2) }final ${ this.className } event = new ${ this.className }(\n`;

        content += this.args.map(arg => EventConstructor.indent(3) + this.superCallValueFor(arg)).join(",\n") + ");\n\n";
        content += EventConstructor.indent(2) + "EventLoggerImpl.Send(event);\n"

        content += `${ EventConstructor.indent(1) }}\n`;

        return content;
    }

    private superCallValueFor(arg: Argument) {
        switch (arg.name) {
            case "eventName":
                return `EventName.${ EventConstructor.toSnakeCase(this.className).toUpperCase() }`;

            case "common":
                return "(Common) EventsStore.common()";

            case "user":
                return "(User) EventsStore.user()";

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

namespace parser {
    class Method {
        public readonly signature: string;

        protected readonly methodName: string;

        constructor(signature: string, methodName: string) {
            this.signature = signature;
            this.methodName = methodName;
        }
    }

    class Constructor extends Method {
        public readonly args: string;

        static from(className: string, content: string): Constructor[] {
            const ctors = [] as Constructor[];
            const regex = new RegExp("public (" + className + ")\\((.+)\\)", "g");

            let match: RegExpExecArray | null = null;
            while ((match = regex.exec(content)) !== null) {
                ctors.push(new Constructor(match));
            }

            return ctors;
        }

        private constructor(match: RegExpExecArray) {
            super(match[0], match[1]);
            this.args = match[2];
        }

        public arguments(): Array<{ name: string; type: string; }> {
            return this.args
                .split(/\s*,\s*/)
                .map(arg => arg.split(" ") as [string, string])
                .map(([type, name]) => ({ type, name }));
        }
    }

    class Getter extends Method {
        private static readonly REG_EX = /public\s+([\w\d_\-\.]+)\s+get([\w\d_\-\.]+)\(\)/g;

        private readonly returnType: string;

        static from(content: string): Getter[] {
            const getters = [] as Getter[];
            Getter.REG_EX.lastIndex = 0;

            let match: RegExpExecArray | null = null;
            while ((match = Getter.REG_EX.exec(content)) !== null) {
                getters.push(new Getter(match));
            }

            return getters;
        }

        private constructor(match: RegExpExecArray) {
            super(match[0], match[2]);
            this.returnType = match[1];
        }
    }

    class Setter extends Method {
        private static readonly REG_EX = /public\s+void\s+set([\w\d_\-\.]+)\(([\w\d_\-\.]+)\s+([\w\d_\-\.]+)\)/g;

        public readonly argumentName: string;
        public readonly argumentType: string;

        static from(content: string): Setter[] {
            const setters = [] as Setter[];
            Setter.REG_EX.lastIndex = 0;

            let match: RegExpExecArray | null = null;
            while ((match = Setter.REG_EX.exec(content)) !== null) {
                setters.push(new Setter(match));
            }

            return setters;
        }

        private constructor(match: RegExpExecArray) {
            super(match[0], match[1]);

            this.argumentType = match[2];
            this.argumentName = match[3];
        }

        public getDynamicValueSetterSignature(): string {
            return this.signature.replace(this.argumentType, `EventsStore.DynamicValue<${ this.argumentType }>`);
        }
    }

    abstract class CommonSourceCode {
        protected readonly file: string;
        protected readonly package: string[];
        protected readonly className: string;
        protected readonly getters: Getter[];
        protected readonly setters: Setter[];

        protected constructor(file: string, packages: string[], className: string, getters: Getter[], setters: Setter[]) {
            this.file = file;
            this.package = packages;
            this.getters = getters;
            this.setters = setters;
            this.className = className;
        }

        abstract write(): void;

        protected packageLine(): string {
            return "package " + this.package.join(".") + ";\n\n";
        }

        protected imports() {
            return "";
        }
    }

    class CommonClass extends CommonSourceCode {
        private readonly ctors: Constructor[];
        private readonly content: string;

        constructor(content: string, file: string, packages: string[], className: string, getters: Getter[], setters: Setter[]) {
            super(file, packages, className, getters, setters);

            this.content = content;
            this.ctors = Constructor.from(this.className, this.content);
        }

        write() {
            const newContent = this.content.replace(
                `class ${ this.className }`,
                `class ${ this.className } implements ${ this.className }Interface`);

            writeFileSync(this.file, newContent, "utf-8");
        }
    }

    class CommonClassProxy extends CommonSourceCode  {
        private readonly ctorArguments: Array<{ name: string; type: string; }>;

        constructor(ctor: Constructor, file: string, packages: string[], className: string, getters: Getter[], setters: Setter[]) {
            super(file, packages, className, getters, setters);
            this.ctorArguments = ctor.arguments();
        }

        write() {
            let content = this.packageLine();
            content += this.imports();
            content += `public class ${ this.className }Proxy implements ${ this.className }Interface {\n`;

            content += indent(1) + `public ${ this.className } snapshot() {\n`;
            content += indent(2) + `return new ${ this.className }(\n`;
            content += this.ctorArguments.map(arg => {
                const getter = this.getters.find(item => new RegExp(`get${ arg.name }`, "i").test(item.signature))!;
                const getterName = getter.signature.match(/public\s+[\w\d\.-_]+\s+([\w\d\.-_]+)\(\)/)![1];
                //return indent(3) + `this.${ arg.name }`;
                return indent(3) + `this.${ getterName }()`;
            }).join(",\n") + ");\n";
            content += indent(1) + "}\n\n";

            for (let i = 0; i < this.getters.length; i++) {
                content += CommonClassProxy.section(this.getters[i], this.setters[i]);
            }

            content += "}\n";
            writeFileSync(this.file, content, "utf-8");
        }

        protected imports() {
            return "import " + this.package.slice(0, this.package.length - 1).concat(["EventsStore"]).join(".") + ";\n\n";
        }

        private static section(getter: Getter, setter: Setter): string {
            const argumentName = setter.argumentName;
            const argumentType = setter.argumentType;
            const dynamicArgumentName = "dynamic" + argumentName[0].toUpperCase() + argumentName.substring(1);
            const dynamicArgumentType = `EventsStore.DynamicValue<${ argumentType }>`;


            let content = indent(1) + `private ${ argumentType } ${ argumentName };\n`;
            content += indent(1) + `private ${ dynamicArgumentType } ${ dynamicArgumentName };\n`;

            content += indent(1) + getter.signature + " {\n";
            content += indent(2) + `return this.${ argumentName } == null ? this.${ argumentName } : this.${ dynamicArgumentName }.get();\n`;
            content += indent(1) + "}\n";

            content += indent(1) + setter.signature + " {\n";
            content += indent(2) + `this.${ argumentName } = ${ argumentName };\n`;
            content += indent(1) + "}\n";

            content += indent(1) + setter.signature.replace(argumentType, dynamicArgumentType) + " {\n";
            content += indent(2) + `this.${ dynamicArgumentName } = ${ argumentName };\n`;
            content += indent(1) + "}\n\n";

            return content;
        }
    }

    class CommonClassReadonlyInterface extends CommonSourceCode  {
        constructor(file: string, packages: string[], className: string, getters: Getter[], setters: Setter[]) {
            super(file, packages, className, getters, setters);
        }

        write() {
            let content = this.packageLine();
            content += this.imports();
            content += `public interface ${ this.className }Readonly {\n`;
            for (let i = 0; i < this.getters.length; i++) {
                content += indent(1) + removePublic(this.getters[i].signature) + ";\n\n";
            }

            content += "}\n";
            writeFileSync(this.file, content, "utf-8");
        }
    }

    class CommonClassInterface extends CommonSourceCode  {
        constructor(file: string, packages: string[], className: string, getters: Getter[], setters: Setter[]) {
            super(file, packages, className, getters, setters);
        }

        write() {
            let content = this.packageLine();
            content += this.imports();
            content += `public interface ${ this.className }Interface extends ${ this.className }Readonly {\n`;
            for (let i = 0; i < this.getters.length; i++) {
                content += indent(1) + removePublic(this.setters[i].signature) + ";\n\n";
            }

            content += "}\n";
            writeFileSync(this.file, content, "utf-8");
        }
    }

    export class CommonClassParser {
        private readonly file: string;
        private readonly content: string;
        private readonly package: string[];
        private readonly className: string;

        private readonly ctors: Constructor[];
        private readonly getters: Getter[];
        private readonly setters: Setter[];

        public constructor(path: string) {
            this.file = path;
            this.content = readFileSync(path, "utf8");
            this.className = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
            this.package = this.content.match(/package ([\w\d\.]+)\s*;/)![1].split(".");

            this.ctors = Constructor.from(this.className, this.content);
            this.getters = Getter.from(this.content);
            this.setters = Setter.from(this.content);
        }

        public commitChanges() {
            new CommonClass(
                this.content,
                this.file,
                this.package,
                this.className,
                this.getters,
                this.setters).write();

            new CommonClassReadonlyInterface(
                this.file.replace(this.className, this.className + "Readonly"),
                this.package,
                this.className,
                this.getters,
                this.setters).write();

            new CommonClassInterface(
                this.file.replace(this.className, this.className + "Interface"),
                this.package,
                this.className,
                this.getters,
                this.setters).write();

            new CommonClassProxy(
                this.ctors[0],
                this.file.replace(this.className, this.className + "Proxy"),
                this.package,
                this.className,
                this.getters,
                this.setters).write();
        }
    }

    function indent(level: 1 | 2 | 3): string {
        let content = "";
        while (content.length < level * 4) {
            content += "    ";
        }
        return content;
    }

    function removePublic(signature: string): string {
        return signature.replace(/public\s+/, "");
    }
}

/**
 * Modifies a source file after it was generated
 *
 * @param path for the generated source file
 * @returns {string} the new content for the file
 */
function processSourceFile(className: string, path: string): string {
    let content = readFileSync(path, "utf8");

    content = content
        .replace(/(import [^;]+;)/, AUGEMENTED_BY_SCRIPT_COMMENT
            + "import com.kin.ecosystem.bi.Event;\n"
            + "import com.kin.ecosystem.bi.EventLoggerImpl;\n"
            + `import com.kin.ecosystem.bi.EventsStore;\n\n$1`);

    const ctorRegEx = new RegExp("public " + className + "\\((.+)\\)", "g");
    const ctors = [] as EventConstructor[];

    let match: RegExpExecArray | null = null;
    while ((match = ctorRegEx.exec(content)) !== null) {
        ctors.push(new EventConstructor(className, match[1]));
    }

    const classDef = `public class ${ className } {\n`;
    const newClassDef = `public class ${ className } implements Event {\n`;
    return content
        .replace(classDef, newClassDef + ctors
            .map(ctor => ctor.createFactory() + "\n" + ctor.createSender())
            .join("\n"));
}

/**
 * Processes the source files after they were generated to add our own flavour.
 *
 * @returns {Promise<void>}
 */
async function postProcess() {
    const files = readdirSync(`${ TMP_PATH }/${ PACKAGE_PATH }`);
    files.forEach(file => {
        const className = file.substring(0, file.lastIndexOf("."));
        file = `${ TMP_PATH }/${ PACKAGE_PATH }/${ file }`;

        if (className.endsWith("_")) {
            unlinkSync(file);
        } else if (["User", "Common"].includes(className)) {
            // parser.processSharedSourcedFiles(path);
            new parser.CommonClassParser(file).commitChanges();
        } else {
            writeFileSync(file, processSourceFile(className, file), "utf8");
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
