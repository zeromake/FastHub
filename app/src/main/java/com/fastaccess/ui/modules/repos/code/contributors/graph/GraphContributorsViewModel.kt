package com.fastaccess.ui.modules.repos.code.contributors.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fastaccess.data.service.RepoService
import com.fastaccess.provider.crash.Report
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class GraphContributorsViewModel(
    private val repoService: RepoService,
    private val owner: String,
    private val repo: String
) : ViewModel() {
    lateinit var contributions: MutableStateFlow<GraphStatModel>
    val error = MutableStateFlow<String?>(null)
    init {
        viewModelScope.launch {
            try {
                val model = repoService.getContributorsStats(owner, repo)
                val response = model.string()
                model.close()
                val statsModel : GraphStatModel = Gson().fromJson(response, object : TypeToken<GraphStatModel>() {}.type)
                contributions = MutableStateFlow(statsModel)
            } catch (e: Exception) {
                Report.reportCatchException(e)
                error.value = e.message
            }

        }
    }
}

@Suppress("UNCHECKED_CAST")
class GraphContributorsViewModelFactory(
    private val provider: RepoService,
    private val owner: String,
    private val repo: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GraphContributorsViewModel::class.java)) {
            GraphContributorsViewModel(provider, owner, repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}