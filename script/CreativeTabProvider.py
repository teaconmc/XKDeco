from TableDataProvider import TableDataProvider


class CreativeTabProvider(TableDataProvider):
    def __init__(self, pack):
        super().__init__(pack, 'assets/{}/kiwi/creative_tab', 'creative_tabs')
        self.tabCount = 0
        self.contents = {}

    def generateRow(self, row, csvConfig):
        if row['ID'] == '':
            return
        data = {}
        self.tabCount += 1
        data['order'] = self.tabCount
        data['icon'] = str(self.pack.defaultResourceLocation(row['Icon']))
        if row['ID'] in self.contents:
            data['contents'] = self.contents[row['ID']]
        translationKey = 'itemGroup.{namespace}.{name}'.format(namespace=self.pack.config['namespace'], name=row['ID'])
        if 'Name:en_us' in row and row['Name:en_us'] != '':
            self.pack.providers['translations'].putTranslation('en_us', translationKey, row['Name:en_us'])
        if 'SecondaryName' in csvConfig:
            fieldName = 'Name:' + csvConfig['SecondaryName']
            if fieldName in row and row[fieldName] != '':
                self.pack.providers['translations'].putTranslation(csvConfig['SecondaryName'], translationKey, row[fieldName])

        self.writeFile(self.pack.defaultResourceLocation(row['ID']), data)

    def addContent(self, tabId, content):
        if tabId not in self.contents:
            self.contents[tabId] = []
        self.contents[tabId].append(str(content))