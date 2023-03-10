package ua.cn.stu.http.sources.boxes

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import okhttp3.Request
import ua.cn.stu.http.app.model.boxes.BoxesSource
import ua.cn.stu.http.app.model.boxes.entities.BoxAndSettings
import ua.cn.stu.http.app.model.boxes.entities.BoxesFilter
import ua.cn.stu.http.sources.base.BaseOkHttpSource
import ua.cn.stu.http.sources.base.OkHttpConfig
import ua.cn.stu.http.sources.boxes.entities.GetBoxResponseEntity
import ua.cn.stu.http.sources.boxes.entities.UpdateBoxRequestEntity

class OkHttpBoxesSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config), BoxesSource {

    override suspend fun setIsActive(boxId: Long, isActive: Boolean) {
        val updateBoxRequestEntity = UpdateBoxRequestEntity(isActive = isActive)
        val request = Request.Builder()
            .put(updateBoxRequestEntity.toJsonRequestBody())
            .endpoint("/boxes/${boxId}")
            .build()
        client.newCall(request).suspendEnqueue()
    }

    override suspend fun getBoxes(boxesFilter: BoxesFilter): List<BoxAndSettings> {
        delay(500)
        // Call "GET /boxes?active=true" if boxesFilter = ONLY_ACTIVE.
        // Call "GET /boxes" if boxesFilter = ALL.
        // Hint: use TypeToken for converting server response into List<GetBoxResponseEntity>
        // Hint: use GetBoxResponseEntity.toBoxAndSettings for mapping GetBoxResponseEntity into BoxAndSettings
        val args = if (boxesFilter == BoxesFilter.ONLY_ACTIVE) "?active=true" else ""
        val request = Request.Builder()
            .get()
            .endpoint("/boxes?$args")
            .build()
        val call = client.newCall(request)
        val typeToken = object : TypeToken<List<GetBoxResponseEntity>>() {}
        val response = call.suspendEnqueue().parseJsonResponse(typeToken)
        return response.map { it.toBoxAndSettings() }
    }
}