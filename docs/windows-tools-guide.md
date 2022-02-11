# Windows tools installation guide

As explained in the [docs](https://github.com/fvarrui/JavaPackager#generated-artifacts), you must install [Inno Setup (iscc)](https://jrsoftware.org/isinfo.php) to generate an EXE installer and [WIX Toolset (candle and light)](https://wixtoolset.org/) to generate a MSI file.

You can install both tools in a simple way using [Chocolatey](https://chocolatey.org/) package manager:

1. [Install Chocolatey](https://chocolatey.org/install).

2. Run next command on CMD or PowerShell as Administrator to install both tools:
   
   ```bash
   choco install -y innosetup wixtoolset
   ```

> And both tools will be automatically available in `PATH`.
