package com.example.trello.Network.TrelloClient

import com.example.trello.Network.TrelloService.OrganizationData
import io.reactivex.Completable
import io.reactivex.Single

interface OrganizationInteraction {
    fun loadOrganization(id: String = "me"): Single<Array<OrganizationData>>
    fun deleteOrganization(idOrganization : String): Completable
}