package com.example.trello.Data

interface OrganizationInteraction : BoardInteraction {
    fun requestAllData()
    fun requestBoards()
    fun addBoard(name : String, org_name: String)

}