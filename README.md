 # Devkit Wallet

<p align="center">
    <img src="./screenshots.png" width="700">
</p>

The _Devkit Wallet_ is a wallet built as a reference app for the [bitcoindevkit](https://github.com/bitcoindevkit) on Android. It is a fork of the long-standing [Devkit Wallet](https://github.com/thunderbiscuit/devkit-wallet), a repository showcasing the bitcoindevkit library for beginner and advanced Android developers. This repository is not intended produce a production-ready wallet, and the apps only work on Testnet3, Testnet4, Signet, and Regtest.

The demo apps are built with the following goals in mind:
1. Be a reference application for the bitcoindevkit API on Android.
2. Showcase some of the more advanced features of the bitcoindevkit library.

## Variants

The app is available in a few variants, each showcasing different features or versions of the bitcoindevkit libraries. The variants each have their own directory, and are standalone applications by themselves. After cloning the repository, developers should open the variants in their own Android Studio instance.

- **[Variant — Esplora](https://github.com/bitcoindevkit/devkit-wallet/tree/master/Variant%20%E2%80%94%20Esplora):** The default branch. This app receives its bitcoin data from a public Esplora instance.
- **[Variant — Kyoto](https://github.com/bitcoindevkit/devkit-wallet/tree/master/Variant%20%E2%80%94%20Kyoto):** This app uses Compact Block Filters to sync its wallet.
- **[Variant — 1.0](https://github.com/bitcoindevkit/devkit-wallet/tree/master/Variant%20%E2%80%94%201.0):** This app  uses the `1.2.0` version of the bitcoindevkit library, showcasing the 1.0 API.
- **[Variant — 0.32](https://github.com/bitcoindevkit/devkit-wallet/tree/master/Variant%20%E2%80%94%200.30):** This app  uses the `0.32.1` version of the bitcoindevkit library, showcasing the pre-1.0 API.
