package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.GitHubPackage
import com.fastaccess.data.entity.GitHubPackage_
import com.fastaccess.utils.equal
import com.fastaccess.utils.toSingle
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single

class GitHubPackageDao {
    companion object {
        val box: Box<GitHubPackage> by lazy { ObjectBox.boxStore.boxFor() }

        fun save(entity: GitHubPackage): Single<Long> {
            return box.toSingle {
                it.put(entity)
            }
        }

        fun save(models: List<GitHubPackage>, id: Long): Single<Unit> {
            return box.toSingle { it.remove(id) }.flatMap {
                box.toSingle { it.put(models)}
            }
        }
        fun getPackagesOf(ownerName: String, packageType: String): Single<List<GitHubPackage>> {
            return box.query()
                .equal(GitHubPackage_.owner, ownerName)
                .equal(GitHubPackage_.package_type, packageType)
                .build()
                .toSingle {
                    it.find()
                }
        }
    }
}
