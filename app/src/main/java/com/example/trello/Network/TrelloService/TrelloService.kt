package com.example.trello.Network.TrelloService


import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface TrelloService {
    @GET("members/{id}/organizations")
    fun getOrganizations(
        @Path("id") id: String = "me",
        @Query("filter") filter: String = "all",
        @Query("fields") fields: String = "id,displayName",
        @Query("paid_account") paid_account: String = "false"
    ): Single<Array<OrganizationData>>

    @GET("members/{id}/boards")
    fun getBoards(
        @Path("id") id: String = "me",
        @Query("filter") filter: String = "all",
        @Query("fields") fields: String = "id,name,idOrganization",
        @Query("lists") lists: String = "none",
        @Query("memberships") memberships: String = "none",
        @Query("organization") organization: String = "false",
        @Query("organization_fields") organization_fields: String = "name,displayName"
    ): Single<Array<BoardData>>

    @GET("boards/{idBoard}/lists")
    fun getLists(
        @Path("idBoard") idBoard: String,
        @Query("cards") cards: String = "none",
        @Query("card_fields") card_fields: String = "all",
        @Query("filter") filter: String = "open",
        @Query("fields") fields: String = "id,name"
    ): Single<Array<ListData>>

    @GET("boards/{idBoard}/cards")
    fun getCards(
        @Path("idBoard") idBoard: String,
        @Query("fields") fields: String = "id,idList,name"
    ): Single<Array<CardData>>

    @POST("boards/")
    fun createBoard(
        @Query("name") name: String,
        @Query("defaultLabels") defaultLabels: String = "true",
        @Query("defaultLists") defaultLists: String = "true",
        @Query("idOrganization") idOrganization: String,
        @Query("keepFromSource") keepFromSource: String = "none",
        @Query("prefs_permissionLevel") prefs_permissionLevel: String = "private",
        @Query("prefs_voting") prefs_voting: String = "disabled",
        @Query("prefs_comments") prefs_comments: String = "members",
        @Query("prefs_invitations") prefs_invitations: String = "members",
        @Query("prefs_selfJoin") prefs_selfJoin: String = "true",
        @Query("prefs_cardCovers") prefs_cardCovers: String = "true",
        @Query("prefs_background") prefs_background: String = "blue",
        @Query("prefs_cardAging") prefs_cardAging: String = "regular"
    ): Completable

    @POST("lists")
    fun createList(
        @Query("name") name: String,
        @Query("idBoard") idBoard: String,
        @Query("pos") pos: String = "top"
    ): Completable

    @POST("cards")
    fun createCard(
        @Query("name") name: String,
        @Query("idList") idList: String,
        @Query("pos") pos: String = "top"
    ): Completable

    @PUT("cards/{idCard}")
    fun updateCard(
        @Path("idCard") idCard: String,
        @Query("idList") idList: String,
        @Query("pos") pos: String = "top"
    ): Completable

    @PUT("lists/{idList}/closed")
    fun archiveList(
        @Path("idList") idList: String,
        @Query("value") value: String
    ): Completable

    @DELETE("organizations/{idOrganization}")
    fun deleteOrganization(
        @Path("idOrganization") idOrganization: String
    ): Completable

    @DELETE("boards/{idBoard}")
    fun deleteBoard(
        @Path("idBoard") idBoard: String
    ): Completable

    @DELETE("cards/{idCard}")
    fun deleteCard(
        @Path("idCard") idCard: String
    ): Completable
}