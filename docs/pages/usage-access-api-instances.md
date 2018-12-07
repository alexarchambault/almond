---
title: Access API instances
---

## From the REPL

These can be accessed directly by their names, like
```
interp
repl
kernel
```

or via implicits, like
```
implicitly[ammonite.interp.InterpAPI]
implicitly[ammonite.repl.ReplAPI]
implicitly[almond.api.JupyterAPI]
```

## From a library

To access API instances from libraries, write methods looking for implicit
API instances, like
```
def displayTimer()(implicit kernel: almond.api.JupyterApi): Unit = {
  val count = 5
  val id = java.util.UUID.randomUUID().toString
  kernel.publish.html(s"<b>$count</b>", id)
  for (i <- (0 until count).reverse) {
    Thread.sleep(1000L)
    kernel.publish.updateHtml(s"<b>$i</b>", id)
  }
  Thread.sleep(200L)
  kernel.publish.updateHtml(Character.toChars(0x1f981).mkString, id)
}
```

Users can then load the library defining this method, and call it themselves
from a notebook. The library can interact with the front-end, without overhead
for users.

A library defining this method, along with a notebook using it, is available
in [this repository](https://github.com/almond-sh/example-library-jupyter-api).
