 # Devkit Wallet

<p align="center">
    <img src="./screenshots.png" width="700">
</p>

The _Devkit Wallet_ is a wallet built as a reference app for the [bitcoindevkit](https://github.com/bitcoindevkit) on Android. It is a fork of the long-standing [Devkit Wallet](https://github.com/thunderbiscuit/devkit-wallet), a repository showcasing the bitcoindevkit library for beginner and advanced Android developers. This repository is not intended produce a production-ready wallet, and the apps only work on Testnet3, Testnet4, Signet, and Regtest.

The demo apps are built with the following goals in mind:
1. Be a reference application for the bitcoindevkit API on Android.
2. Showcase some of the more advanced features of the bitcoindevkit library.

## Variants

The app is available in a few variants, each showcasing different features or versions of the bitcoindevkit libraries. Each variant lives on its own branch and is a standalone application. After cloning the repository and checking out a variant branch, developers should open it in Android Studio.

- **[master](https://github.com/bitcoindevkit/devkit-wallet/tree/master):** This app uses Compact Block Filters and a few other options to sync its wallet.
- **[variant/2.0](https://github.com/bitcoindevkit/devkit-wallet/tree/variant/2.0):** This app uses the `2.x` version of the bitcoindevkit library, showcasing the 2.0 API. This app receives its bitcoin data from a public Esplora instance.
- **[variant/1.0](https://github.com/bitcoindevkit/devkit-wallet/tree/variant/1.0):** This app uses the `1.x` version of the bitcoindevkit library, showcasing the 1.0 API.
- **[variant/0.32](https://github.com/bitcoindevkit/devkit-wallet/tree/variant/0.32):** This app uses the `0.32.x` version of the bitcoindevkit library, showcasing the pre-1.0 API.
