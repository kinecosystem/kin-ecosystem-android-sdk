#!/usr/bin/env node

import { readdirSync, unlinkSync, existsSync, readFileSync, writeFileSync } from "fs";
import { exec } from"child_process";

const SOURCE_PATH = "../../kin-bi/json_schemas/client";
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
    private static readonly AUGMENTED_ARGS = ["eventName", "eventType", "common", "user", "client"];

    private readonly className: string;
    private readonly args: Argument[];

    public readonly source: string;

    constructor(className: string, args: string, source: string) {
        this.className = className;
        this.args = args
            .split(EventConstructor.ARGS_REG_EX)
            .map(arg => arg.split(/\s+/))
            .map(arg => new Argument(arg[0], arg[1]));

        const endToken = "\n    }\n";
        let startIndex = source.search(new RegExp(`\\n    public ${ className }\\([\\w\\d]+`));
        startIndex = source.lastIndexOf("    /**", startIndex);
        const endIndex = source.indexOf(endToken, startIndex) + endToken.length;
        this.source = source.substring(startIndex, endIndex);

        // needed because it is passed as a callback
        this.superCallValueFor = this.superCallValueFor.bind(this);
    }

    public newSource(): string {
        return this.source
            .replace("     * @param eventName", "")
            .replace("     * @param eventType", "")
            .replace(`${ this.className }.EventName eventName, `, "")
            .replace(`${ this.className }.EventType eventType, `, "")
            .replace(/\s+this\.eventName = eventName;/, "")
            .replace(/\s+this\.eventType = eventType;/, "");
    }

    public createFactory(): string {
        let content = EventConstructor.indent(1) + AUGEMENTED_BY_SCRIPT_COMMENT;
        content += `${ EventConstructor.indent(1) }public static ${ this.className } create(`;

        content += this.args
            .filter(arg => !EventConstructor.AUGMENTED_ARGS.includes(arg.name))
            .join(", ");

        content += ") {\n";
        content += `${ EventConstructor.indent(2) }return new ${ this.className }(\n`;

        content += this.args
            .filter(arg => arg.name !== "eventName" && arg.name !== "eventType")
            .map(arg => EventConstructor.indent(3) + this.superCallValueFor(arg))
            .join(",\n") + ");\n";

        content += `${ EventConstructor.indent(1) }}`;

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

        content += this.args
            .filter(arg => arg.name !== "eventName" && arg.name !== "eventType")
            .map(arg => EventConstructor.indent(3) + this.superCallValueFor(arg))
            .join(",\n") + ");\n\n";
        content += EventConstructor.indent(2) + "EventLoggerImpl.Send(event);\n"

        content += `${ EventConstructor.indent(1) }}\n`;

        return content;
    }

    private superCallValueFor(arg: Argument) {
        switch (arg.name) {
            case "eventName":
                return `EventName.${ EventConstructor.toSnakeCase(this.className).toUpperCase() }`;

            case "user":
                return "(User) EventsStore.user()";

            case "common":
                return "(Common) EventsStore.common()";

            case "client":
                return "(Client) EventsStore.client()";

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

class EventSourceEnum {
    public readonly name: string;
    public readonly source: string;
    public readonly values: string[];

    static contains(name: string, source: string): boolean {
        return source.indexOf(`public enum ${ name }`) > 0;
    }

    constructor(name: string, source: string) {
        this.name = name;

        const endToken = "\n    }\n";
        const startIndex = source.indexOf(`\n    public enum ${ name }`);
        const endIndex = source.indexOf(endToken, startIndex) + endToken.length;
        this.source = source.substring(startIndex, endIndex);

        this.values = [];

        let match: RegExpExecArray | null;
        const regex = /@SerializedName\("([\w\d]+)"\)/g;
        while ((match = regex.exec(this.source)) !== null) {
            this.values.push(match[1]);
        }
    }
}

namespace parser {
    class Method {
        public readonly signature: string;
        public readonly methodName: string;

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

            let match: RegExpExecArray | null;
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

            let match: RegExpExecArray | null;
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

        protected imports(): string[] {
            return [];
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
            let newContent = this.content;

            const platformEnum = EventSourceEnum.contains("Platform", this.content) ? new EventSourceEnum("Platform", this.content) : null;

            if (platformEnum) {
                const regex = /android/i;
                const platformValue = platformEnum.values.filter(value => regex.test(value))[0];

                newContent = newContent
                    .replace(
                        /public class Common[^\n]+\n/,
                        `$&    public static final String PLATFORM = "${ platformValue }";\n`)
                    .replace(platformEnum.source, "")
                    .replace(/\n\s+\* @param platform/, "")
                    .replace("private Common.Platform platform;", "private String platform = PLATFORM;")
                    .replace(", Common.Platform platform", "")
                    .replace(/\n\s+this.platform = platform;/, "")
                    .replace("public Common.Platform getPlatform()", "public String getPlatform()")
                    .replace("public void setPlatform(Common.Platform platform)", "public void setPlatform(String platform)");
            }

            newContent = newContent.replace(
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
            content += this.imports().map(path => `import ${ path };`).join("\n") + "\n\n";
            content += `public class ${ this.className }Proxy implements ${ this.className }Interface {\n`;

            content += indent(1) + `public ${ this.className } snapshot() {\n`;
            content += indent(2) + `return new ${ this.className }(\n`;
            content += this.ctorArguments.map(arg => {
                const getter = this.getters.find(item => new RegExp(`get${ arg.name }`, "i").test(item.signature))!;
                if (getter.methodName === "Platform") {
                    return "";
                }
                const getterName = getter.signature.match(/public\s+[\w\d\.-_]+\s+([\w\d\.-_]+)\(\)/)![1];
                return indent(3) + `this.${ getterName }()`;
            }).filter(item => item !== "").join(",\n") + ");\n";
            content += indent(1) + "}\n\n";

            for (let i = 0; i < this.getters.length; i++) {
                content += CommonClassProxy.section(this.getters[i], this.setters[i]);
            }

            content += "}\n";
            writeFileSync(this.file, content, "utf-8");
        }

        protected imports() {
            const imports = super.imports();

            if (this.className.startsWith("Common")) {
                imports.push("java.util.UUID");
            }

            imports.push(this.package.slice(0, this.package.length - 1).concat(["EventsStore"]).join("."));

            return imports;
        }

        private static section(getter: Getter, setter: Setter): string {
            const argumentName = setter.argumentName;
            const argumentType = setter.methodName === "Platform" ? "String" : setter.argumentType;

            const dynamicArgumentName = "dynamic" + argumentName[0].toUpperCase() + argumentName.substring(1);
            const dynamicArgumentType = `EventsStore.DynamicValue<${ argumentType }>`;

            const getterSignature = getter.methodName === "Platform" ? "public String getPlatform()" : getter.signature;
            const setterSignature = setter.methodName === "Platform" ? "public void setPlatform(String platform)" : setter.signature;


            let content = indent(1) + `private ${ argumentType } ${ argumentName };\n`;
            content += indent(1) + `private ${ dynamicArgumentType } ${ dynamicArgumentName };\n`;

            content += indent(1) + getterSignature + " {\n";
            content += indent(2) + `return this.${ argumentName } != null ? this.${ argumentName } : this.${ dynamicArgumentName }.get();\n`;
            content += indent(1) + "}\n";

            content += indent(1) + setterSignature + " {\n";
            content += indent(2) + `this.${ argumentName } = ${ argumentName };\n`;
            content += indent(1) + "}\n";

            content += indent(1) + setterSignature.replace(argumentType, dynamicArgumentType) + " {\n";
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
            content += this.imports().map(path => `import ${ path };`).join("\n") + "\n\n";
            content += `public interface ${ this.className }Readonly {\n`;
            for (let i = 0; i < this.getters.length; i++) {
                if (this.getters[i].methodName === "Platform") {
                    content += indent(1) + "String getPlatform();\n\n";
                } else {
                    content += indent(1) + removePublic(this.getters[i].signature) + ";\n\n";
                }
            }

            content += "}\n";
            writeFileSync(this.file, content, "utf-8");
        }

        protected imports() {
            const imports = super.imports();

            if (this.className.startsWith("Common")) {
                imports.push("java.util.UUID");
            }

            return imports;
        }
    }

    class CommonClassInterface extends CommonSourceCode  {
        constructor(file: string, packages: string[], className: string, getters: Getter[], setters: Setter[]) {
            super(file, packages, className, getters, setters);
        }

        write() {
            let content = this.packageLine();
            content += this.imports().map(path => `import ${ path };`).join("\n") + "\n\n";
            content += `public interface ${ this.className }Interface extends ${ this.className }Readonly {\n`;
            for (let i = 0; i < this.setters.length; i++) {
                if (this.setters[i].methodName === "Platform") {
                    content += indent(1) + "void setPlatform(String platform);\n\n";
                } else {
                    content += indent(1) + removePublic(this.setters[i].signature) + ";\n\n";
                }
            }

            content += "}\n";
            writeFileSync(this.file, content, "utf-8");
        }

        protected imports() {
            const imports = super.imports();

            if (this.className.startsWith("Common")) {
                imports.push("java.util.UUID");
            }

            return imports;
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

    const eventNameEnum = new EventSourceEnum("EventName", content);
    const eventTypeEnum = new EventSourceEnum("EventType", content);

    content = content
        .replace(/(import [^;]+;)/, AUGEMENTED_BY_SCRIPT_COMMENT
            + "import com.kin.ecosystem.bi.Event;\n"
            + "import com.kin.ecosystem.bi.EventLoggerImpl;\n"
            + `import com.kin.ecosystem.bi.EventsStore;\n\n$1`);

    const ctorRegEx = new RegExp("public " + className + "\\((.+)\\)", "g");
    const ctors = [] as EventConstructor[];

    let match: RegExpExecArray | null = null;
    while ((match = ctorRegEx.exec(content)) !== null) {
        const ctor = new EventConstructor(className, match[1], content);
        content = content.replace(ctor.source, ctor.newSource());
        ctors.push(ctor);
    }

    const classDef = `public class ${ className } {\n`;
    const newClassDef = `public class ${ className } implements Event {\n`;

    const eventNameDef = `private ${ className }.EventName eventName`;
    const newEventNameDef = `private String eventName = EVENT_NAME`;
    const eventNameGetterDef = `public ${ className }.EventName getEventName()`;
    const newEventNameGetterDef = `public String getEventName()`;
    const eventNameSetterDef = `public void setEventName(${ className }.EventName eventName)`;
    const newEventNameSetterDef = `public void setEventName(String eventName)`;

    const eventTypeDef = `private ${ className }.EventType eventType`;
    const newEventTypeDef = `private String eventType = EVENT_TYPE`;
    const eventTypeGetterDef = `public ${ className }.EventType getEventType()`;
    const newEventTypeGetterDef = `public String getEventType()`;
    const eventTypeSetterDef = `public void setEventType(${ className }.EventType eventType)`;
    const newEventTypeSetterDef = `public void setEventType(String eventType)`;

    return content
        .replace(
            classDef,
            newClassDef
                + `    public static final String EVENT_NAME = "${ eventNameEnum.values[0] }";\n`
                + `    public static final String EVENT_TYPE = "${ eventTypeEnum.values[0] }";\n\n`
                + ctors
                    .map(ctor => ctor.createFactory() + "\n\n" + ctor.createSender())
                    .join("\n"))
        .replace(eventNameDef, newEventNameDef)
        .replace(eventTypeDef, newEventTypeDef)
        .replace(eventNameEnum.source, "")
        .replace(eventNameGetterDef, newEventNameGetterDef)
        .replace(eventNameSetterDef, newEventNameSetterDef)
        .replace(eventTypeEnum.source, "")
        .replace(eventTypeGetterDef, newEventTypeGetterDef)
        .replace(eventTypeSetterDef, newEventTypeSetterDef);
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
        } else if (["User", "Common", "Client"].includes(className)) {
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
