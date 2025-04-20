 # Bitcoindevkit Android Example Wallet

<p align="center">
    <img src="./images/screenshots.png" width="700">
</p>

The _Devkit Wallet_ is a wallet built as a reference app for the [bitcoindevkit](https://github.com/bitcoindevkit) on Android. It is a fork of the long-standing [Devkit Wallet](https://github.com/thunderbiscuit/devkit-wallet), a repository showcasing the bitcoindevkit library for beginner and advanced Android developers. This repository is not intended to be a production-ready wallet, and only works on Testnet3, Testnet4, Signet, and Regtest.

This demo app is a departure of the Devkit Wallet approach and is built with the following goals in mind:
1. Be a reference application for the bitcoindevkit API on Android.
2. Showcase some of the more advanced features of the bitcoindevkit library.

## Variants

The app is available in a few variants, each showcasing different features of the bitcoindevkit library. The variants live on different branches and are as follows:
- **[variant/esplora](https://github.com/bitcoindevkit/devkit-wallet/tree/variant/esplora):** The default branch. This app receives its bitcoin data from a public Esplora instance.
- **[variant/kyoto](https://github.com/bitcoindevkit/devkit-wallet/tree/variant/kyoto):** This app uses Compact Block Filters to sync its wallet.
- **[variant/0.30.0](https://github.com/bitcoindevkit/devkit-wallet/tree/variant/0.30.0):** This app  uses the `0.30.0` version of the bitcoindevkit library, showcasing the pre-1.0 API.
