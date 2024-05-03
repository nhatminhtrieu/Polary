package com.example.polary.`object`

import android.util.Log
import com.example.polary.Class.HttpMethod
import com.example.polary.dataClass.Group
import com.example.polary.utils.ApiCallBack
import java.time.LocalDateTime

object GroupsData {
    var revalidate: Boolean = false
        set(value) {
            field = value
        }

    var updatedAt: LocalDateTime? = null
    private lateinit var groups: List<Group>
    fun getGroups(userId: Int, TAG: String? = "FetchData", onSuccess: (List<Group>?) -> Unit) {
        val now = LocalDateTime.now()
        if (revalidate || updatedAt == null || now.minusMinutes(1).isAfter(updatedAt)) {
            revalidate = false
            // Get the groups from the database
            val httpMethod = HttpMethod()
            httpMethod.doGet<Group>("users/$userId/groups", object : ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    Log.d(TAG, "Successfully fetched groups: $data")
                    groups = data as List<Group>
                    updatedAt = now
                    onSuccess(groups)
                }

                override fun onError(error: Throwable) {
                    Log.e(TAG, "Failed to fetch groups: $error")
                }
            })
        }
        else {
            onSuccess(groups)
        }
    }

    fun getGroupsWithMembers(userId: Int, TAG: String? = "FetchData", onSuccess: (List<Group>?) -> Unit) {
        val httpMethod = HttpMethod()
        httpMethod.doGetWithQuery<Group>("users/$userId/groups", mapOf("include-members" to "true"), object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                Log.d(TAG, "Successfully fetched groups: $data")
                onSuccess(data as List<Group>)
            }

            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch groups: $error")
            }
        })
    }

    fun getGroupById(groupId: String, TAG: String? = "FetchData", onSuccess: (Group?) -> Unit) {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Group>("groups/$groupId", object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                Log.d(TAG, "Successfully fetched group: $data")
                onSuccess(data as Group)
            }

            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch group: $error")
            }
        })
    }

    fun createGroup(ownerId: Int, name: String, memberIds: List<Int>, TAG: String? = "FetchData", onSuccess: () -> Unit) {
        val httpMethod = HttpMethod()
        val requestBody = mapOf(
            "name" to name,
            "memberIds" to memberIds,
            "ownerId" to ownerId
        )
        httpMethod.doPost("groups", requestBody, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                Log.d(TAG, "Successfully created group: $data")
                onSuccess()
            }

            override fun onError(error: Throwable) {
                Log.e("error", "Error: ${error.message}")
            }
        })
    }

    fun editGroup(groupId: Int, name: String, memberIds: List<Int>, TAG: String? = "FetchData", onSuccess: () -> Unit) {
        val httpMethod = HttpMethod()
        val requestBody = mapOf(
            "name" to name,
            "memberIds" to memberIds
        )
        httpMethod.doPut("groups/$groupId", requestBody, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                Log.d(TAG, "Successfully edited group: $data")
                onSuccess()
            }

            override fun onError(error: Throwable) {
                Log.e("error", "Error: ${error.message}")
            }
        })
    }

    fun deleteGroup(groupId: Int, TAG: String? = "FetchData", onSuccess: () -> Unit) {
        val httpMethod = HttpMethod()
        httpMethod.doDelete("groups/$groupId", mapOf(), object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                Log.d(TAG, "Successfully deleted group: $data")
                onSuccess()
            }

            override fun onError(error: Throwable) {
                Log.e("error", "Error: ${error.message}")
            }
        })
    }
}