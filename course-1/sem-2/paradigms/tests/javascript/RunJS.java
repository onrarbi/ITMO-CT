import javax.script.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * JavaScript runner.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class RunJS {
    private RunJS() {
    }

    public static void main(final String[] args) throws ScriptException {
        final String script = args.length == 0 ? "examples.js" : args[0];

        final ScriptEngine engine;
        try {
            System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
            System.setProperty("polyglot.js.strict", "true");

            final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
//            engine = scriptEngineManager.getEngineFactories().stream()
//                    .filter(factory -> "Graal.js".equals(factory.getEngineName()))
//                    .map(ScriptEngineFactory::getScriptEngine)
//                    .findAny().orElse(null);
            engine = scriptEngineManager.getEngineByName("Graal.js");
            if (engine == null) {
                System.err.println("Graal.js not found");
                System.err.println("Use the following command line to run RunJS:");
                System.err.println("java --module-path=graal -cp . RunJS");
                System.err.println("Known engines:");
                for (final ScriptEngineFactory engineFactory : scriptEngineManager.getEngineFactories()) {
                    System.out.println("    " + engineFactory.getEngineName());
                }
                return;
            }

            engine.put("polyglot.js.allowIO", true);
            engine.put("polyglot.js.allowHostAccess", true);
//        engine.put("polyglot.js.ecmascript-version", "2025");
            engine.put("io", new IO(engine));
            engine.put("global", engine.getContext().getBindings(ScriptContext.ENGINE_SCOPE));
            engine.eval("var println = function() { io.println(Array.prototype.map.call(arguments, String).join(' ')); };");
            engine.eval("var print   = function() { io.print  (Array.prototype.map.call(arguments, String).join(' ')); };");
            engine.eval("var include = function(file) { io.include(file); }");
            engine.eval("var readLine = function(prompt) { return io.readLine(prompt); }");
        } catch (final ScriptException e) {
            throw new AssertionError("Invalid initialization", e);
        }
        engine.eval("io.include('" + script + "')");
    }

    @SuppressWarnings({"MethodMayBeStatic", "unused"})
    public static class IO {
        private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        private final ScriptEngine engine;

        public IO(final ScriptEngine engine) {
            this.engine = engine;
        }

        public void print(final String message) {
            System.out.print(message);
        }

        public void println(final String message) {
            System.out.println(message);
        }

        public void include(final String file) throws IOException, ScriptException {
            engine.getContext().setAttribute(ScriptEngine.FILENAME, file, ScriptContext.ENGINE_SCOPE);
            engine.eval(new FileReader(file, StandardCharsets.UTF_8));
        }

        public String readLine(final String prompt) throws IOException {
            if (prompt != null) {
                System.out.print(prompt);
            }
            return reader.readLine();
        }
    }
}
