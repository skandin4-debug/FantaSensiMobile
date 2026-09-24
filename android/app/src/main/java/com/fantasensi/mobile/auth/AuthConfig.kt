package com.fantasensi.mobile.auth

object AuthConfig {
    private val nameEnc = "QGC5OR3t1dZ8yHGJhHAOIuyeqEkE61PifNHItVwk38Q="
    private val ownerEnc = "hOXXBLGvLW6mrjX/m1ARE2x/gddQpzoefctErBYrxd8="
    private val versionEnc = "Wc3s0/HaVH/qDozGkA2XW49s4JELsw9+/wM51UjYJyI="
    private val urlEnc = "CNf4jBG7dxFIyaGhHOi4Ritk10tt2xziNxU2QCoyiLdyJdvo2MHCWXrAv1JBHUeH"

    val name: String get() = Obf.d(nameEnc)
    val ownerId: String get() = Obf.d(ownerEnc)
    val version: String get() = Obf.d(versionEnc)
    val url: String get() = Obf.d(urlEnc)
}
