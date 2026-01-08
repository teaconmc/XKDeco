from Pack import Pack
from TableDataProvider import TableDataProvider


class GlassTypeProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/glass_type', 'glass_types')
        self.glassTypes = {}

    def generateRow(self, row, tableConfig):
        glassTypeId = self.pack.defaultResourceLocation(row['ID'])
        data = {}

        self.field(data, 'SkipRendering', lambda v: False if v.lower() == 'false' else None)
        self.field(data, 'ShadeBrightness', float)
        self.field(data, 'RenderType', str)

        self.glassTypes[glassTypeId] = data
        self.writeFile(glassTypeId, data)
