from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class BlockTemplateProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/template/block', 'block_templates', {'tags'})
        self.tags = {}

    def generateRow(self, row, csvConfig):
        if row['ID'] == '':
            return
        super().generateRow(row, csvConfig)
        if 'tags' in row and row['tags'] != '':
            tags = [ResourceLocation(tag) for tag in row['tags'].split(',')]
            self.tags[self.pack.defaultResourceLocation(row['ID'])] = tags
