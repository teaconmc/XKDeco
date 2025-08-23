from TableDataProvider import TableDataProvider


class CreativeTabProvider(TableDataProvider):
    def __init__(self, pack):
        super().__init__(pack, 'assets/{}/kiwi/creative_tab', 'creative_tabs')
        self.tabCount = 0
        self.contents = {}

    def generateRow(self, row, tableConfig):
        data = {}
        self.tabCount += 1
        data['order'] = self.tabCount
        data['icon'] = str(self.pack.defaultResourceLocation(row['Icon']))
        if row['ID'] in self.contents:
            data['contents'] = self.contents[row['ID']]

        translationKey = 'itemGroup.{namespace}.{name}'.format(namespace=self.pack.config['namespace'], name=row['ID'])
        self.processRowTranslations(row, translationKey)

        self.writeFile(self.pack.defaultResourceLocation(row['ID']), data)

    def addContent(self, tabId, content):
        if tabId not in self.contents:
            self.contents[tabId] = []
        self.contents[tabId].append(str(content))

    def removeContent(self, content):
        content = str(content)
        for tabId in self.contents:
            if content in self.contents[tabId]:
                self.contents[tabId].remove(content)
