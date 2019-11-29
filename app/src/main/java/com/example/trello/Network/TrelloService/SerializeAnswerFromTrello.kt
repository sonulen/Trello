package com.example.trello.Network.TrelloService

class BoardData {
    internal var name: String? = null
    internal var idOrganization: String? = null
    internal var id: String? = null
}

class OrganizationData {
    internal var id: String? = null
    internal var displayName: String? = null
}

class ListData {
    internal var id: String? = null
    internal var name: String? = null
}

class CardData {
    internal var id: String? = null
    internal var idList: String? = null
    internal var name: String? = null
}