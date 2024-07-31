# Keep all classes and their members in the package kz.tarlanpayments.sdk.sdk.data.dto
-keep class kz.tarlanpayments.storage.androidsdk.TarlanContract {
    *;
}

-keep class kz.tarlanpayments.storage.androidsdk.TarlanInput {
    *;
}

# Keep the TarlanOutput$Success and TarlanOutput$Failure classes
-keep class kz.tarlanpayments.storage.androidsdk.TarlanOutput$Success {
    *;
}

-keep class kz.tarlanpayments.storage.androidsdk.TarlanOutput$Failure {
    *;
}

# Keep @Parcelize classes from being obfuscated
-keep @kotlinx.parcelize.Parcelize class * {
    *;
}