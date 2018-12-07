---
title: API
---

The API of almond is two-fold:
- the [Ammonite API](usage-ammonite-api.md) gives access to the REPL internals
(loading dependencies, evaluating code, getting the full classpath),
- the [almond Jupyter API](usage-jupyter-api.md) allows to communicate with
Jupyter front-ends.

Both of these APIs can be used:
- directly from notebook sessions,
- as libraries, that your own libraries can depend on.

In the latter case, your libraries can be loaded during an almond session,
and then interact with the Jupyter front-end.
