import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentVector;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("task-graph.core"));
        // slurping in clojure
        System.out.println(Clojure.var("task-graph.core", "run-with-test-resource").invoke());
        // schlepping in java
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data.edn");
        final String inputs = new String(is.readAllBytes());
        System.out.println(Clojure.var("task-graph.core", "execution-list").invoke(Clojure.read(inputs)));
        // once more for return type interop: cast, cast, cast your vals merrily down gc
        final PersistentVector result =
                (PersistentVector) Clojure.var("task-graph.core", "execution-list").invoke(Clojure.read(inputs));
        for (Object o : result) {
            System.out.println(((String) o).toUpperCase());
        }
    }
}
