---
title: Ammonite API
---

The Ammonite API consists in instances of several classes, that are created
by Ammonite or almond, and allow you to interact with the REPL. These instances
are accessible either by their name, from the REPL, like `interp`,
or via implicits, like `implicitly[ammonite.interp.InterpAPI]`.

The following instances are available:
- `interp`, which is a [`ammonite.interp.InterpAPI`](#interpapi),
- `repl`, which is a [`ammonite.repl.ReplAPI`](#replapi).

## `InterpAPI`

[`InterpAPI`](foo) allows to [load new dependencies](foo) or [scalac plugins](foo),
[add repositories](foo) for dependencies, to [add exit hooks](foo), to
[configure scalac options](foo).

## `ReplAPI`

[`ReplAPI`](foo) allows to [access the latest thrown exception](foo),
[access the command history](foo),
[request the compiler instance to be re-created](foo),
[get the current compiler instance](foo),
[get the current imports](foo),
[evaluate code from strings or files](foo), and lastly
[get the class names and byte code](foo) of the code entered during the current
session.


