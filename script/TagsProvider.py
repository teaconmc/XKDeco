from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class TagsProvider(TableDataProvider):
    def __init__(self, pack: Pack, registry: str, alias: str = None):
        alias = alias if alias is not None else registry
        super().__init__(pack, 'data/{}/tags/' + alias, registry + '_tags')
        self.prettyPrint = True
        self.data = {}

    def addEntry(self, tagKey: ResourceLocation, value: ResourceLocation):
        if tagKey not in self.data:
            self.data[tagKey] = set()
        self.data[tagKey].add(str(value))

    def generateRow(self, row, csvConfig):
        if row['ID'] == '' or row['Values'] == '':
            return
        for value in row['Values'].split(','):
            self.addEntry(ResourceLocation(row['ID']), self.pack.defaultResourceLocation(value))

    def generate(self):
        super().generate()
        for key, value in self.data.items():
            self.writeJson(key, {
                'values': sorted(list(value))
            })

    def __str__(self):
        return 'TagsProvider: ' + self.table
