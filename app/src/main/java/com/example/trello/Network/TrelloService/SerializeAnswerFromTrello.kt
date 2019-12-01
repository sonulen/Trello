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

class CardFullData {
    internal var id: String? = null
    internal var dateLastActivity: String? = null
    internal var desc: String? = null
    internal var name: String? = null
    internal var shortUrl: String? = null
    internal var actions: List<ActionData>? = null
    internal var board: BoardInfoInCard? = null
}

class ActionData {
    internal var type: String? = null
    internal var date: String? = null
    internal var memberCreator: MemberCreatorData? = null
}

class MemberCreatorData {
    internal var fullName: String? = null
    internal var initials: String? = null
    internal var username: String? = null
}

class BoardInfoInCard {
    internal var id: String? = null
    internal var name: String? = null
    internal var idOrganization: String? = null
}