# Changes

## Deprecated plugin options

Next table shows plugin options which have been deprecated and from which plugin version:

| Property                         | Mandatory | Default value   | Description                                                  | Deprecated since |
| -------------------------------- | --------- | --------------- | ------------------------------------------------------------ | ---------------- |
| `forceJreOptimization`           | No        | `false`         | Although JDK version < 13, it will try to reduce the bundled JRE. | v0.8.7           |
| `moduleDependenceAnalysisOption` | No        | `"--list-deps"` | When generating a customized JRE, this option allows to specify a different *Module dependence analysis option* other than the default (`--list-deps`) for `jdeps`. | v0.8.7           |
