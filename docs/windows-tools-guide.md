# Windows tools installation guide

As explained in the [docs](https://github.com/fvarrui/JavaPackager#generated-artifacts), you must install [Inno Setup (iscc)](https://jrsoftware.org/isinfo.php) to generate an EXE installer and [WIX Toolset (candle and light)](https://wixtoolset.org/) to generate a MSI file.

## Using Chocolatey
You can install both tools in a simple way using [Chocolatey](https://chocolatey.org/) package manager:

1. [Install Chocolatey](https://chocolatey.org/install)
2. Run next command on CMD or PowerShell as Administrator to install both tools:
   
   ```bash
   choco install -y innosetup wixtoolset
   ```

> And both tools will be automatically available in `PATH`.

## Using scoop
You can also use [Scoop](https://github.com/ScoopInstaller/Scoop/wiki) to achieve the same goal. Scoop is a lightweight alternative to Chocolatey that doesn't require admin rights and installs by default to a folder in the user's home directory.

1. [Install Scoop](https://scoop.sh/)
2. Run in CMD or PowerShell (no need to be Administrator):

```scoop bucket add extras
scoop install inno-setup
scoop install wixtoolset
```

> Both tools will also be available in `PATH`.
