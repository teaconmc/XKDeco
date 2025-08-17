from Pack import Pack
from TableDataProvider import TableDataProvider


class GlassTypeProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/glass_type', 'glass_types')
        self.glassTypes = {}

    def generateRow(self, row, csvConfig):
        glassTypeId = self.pack.defaultResourceLocation(row['ID'])
        data = {}

        if 'SkipRendering' in row and row['SkipRendering'].lower() == 'false':
            data['skip_rendering'] = False
        if 'ShadeBrightness' in row and row['ShadeBrightness'] != '':
            data['shade_brightness'] = float(row['ShadeBrightness'])
        if 'RenderType' in row and row['RenderType'] != '':
            data['render_type'] = row['RenderType']

        self.glassTypes[glassTypeId] = data
        self.writeFile(glassTypeId, data)
