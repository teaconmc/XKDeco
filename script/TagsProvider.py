from Identifier import Identifier
from TableDataProvider import TableDataProvider


class TagsProvider(TableDataProvider):
    def __init__(self, pack, registry: str):
        super().__init__(pack, 'data/{}/tags/' + registry, registry + '_tags')
        self.prettyPrint = True
        self.exportFormat = 'json'
        self.data = {}

    def addEntry(self, tagKey: Identifier, value: Identifier):
        if tagKey not in self.data:
            self.data[tagKey] = set()
        self.data[tagKey].add(str(value))

    def generateRow(self, row, tableConfig):
        if row['Values'] == '':
            return
        for value in row['Values'].split(','):
            self.addEntry(Identifier(row['ID']), self.pack.defaultIdentifier(value))

    def generate(self):
        super().generate()
        for key, value in self.data.items():
            self.writeFile(key, {
                'values': sorted(list(value))
            })

    def __str__(self):
        return 'TagsProvider: ' + self.table
