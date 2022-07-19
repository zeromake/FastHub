package com.fastaccess.data.dao

/**
 * Created by Kosh on 10.02.18.
 */
class LockIssuePrModel {
    var isLocked = false
    var activeLockReason: String? = null

    constructor() {}
    constructor(locked: Boolean, activeLockReason: String?) {
        isLocked = locked
        this.activeLockReason = activeLockReason
    }
}