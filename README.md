Passbook practice
=================

Sample project to explore Apple's passbook creation in Java.

## Usage

Create the following files in the folder *scr/main/resources*:

- A properties file called *pass.properties* with the following keys:
    - *pass.passTypeIdentifier*: The pass type ID's name, generated on Apple's developer portal.
    - *pass.teamIdentifier*: The team ID associated with your developer account.
    - *keystore.password*: The password of the provided .p12 file.
- A 29x29px png image as *icon.png*
- A .p12 file called *keystore.p12*, created from the signing certificate and the corresponding private key (See "How to use OpenSSL to create an Apple Pass Type Certificate" in the [Sources](#sources) section for more info).
- The certificate itself as *pass.cer* (To be removed since it's redundant, but necessary for now)

The code assumes that the keystore and the private key share the same password. Since this is not always the case, expect it to be changed in future versions.

## Branches
- master: Uses the passkit4j library by Ryan Tenney
- alternative_implementation: Uses my own custom implementation of pkpass creation

## Sources
- [passkit4j by ryantenney](http://www.ryantenney.com/passkit4j/)
- [Wallet Developer Guide](https://developer.apple.com/library/archive/documentation/UserExperience/Conceptual/PassKit_PG/index.html#//apple_ref/doc/uid/TP40012195-CH1-SW1)
- [PassKit Package Format Reference](https://developer.apple.com/library/archive/documentation/UserExperience/Reference/PassKit_Bundle/Chapters/Introduction.html#//apple_ref/doc/uid/TP40012026-CH0-SW1)
- [How to use OpenSSL to create an Apple Pass Type Certificate – Airship Support](https://support.airship.com/hc/en-us/articles/213493603-How-to-use-OpenSSL-to-create-an-Apple-Pass-Type-Certificate)