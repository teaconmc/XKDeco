import BlockPropertiesReader
from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class ItemTemplateProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/template/item', 'item_templates')
        self.tags = {}
        self.properties = {}

    def generateRow(self, row, csvConfig):
        templateId = self.pack.defaultResourceLocation(row['ID'])
        data = {}
        for field in row:
            # only add fields which name is lowercase and not in ignoredFields
            if field.lower() == field and field not in self.ignoredFields and field != '' and row[field] != '':
                data[field] = row[field]

        properties = BlockPropertiesReader.read(row, self.pack)
        if len(properties) > 0:
            self.properties[templateId] = properties
            data['properties'] = properties

        if 'Tags' in row and row['Tags'] != '':
            tags = [ResourceLocation(tag) for tag in row['Tags'].split(',')]
            self.tags[templateId] = tags

        self.writeFile(templateId, data)
