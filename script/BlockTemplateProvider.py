import BlockPropertiesReader
from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class BlockTemplateProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/template/block', 'block_templates')
        self.tags = {}
        self.properties = {}

    def generateRow(self, row, csvConfig):
        templateId = self.pack.defaultResourceLocation(row['ID'])
        data = {}
        for field in row:
            # only add fields which name is lowercase and not in ignoredFields
            if field.lower() == field and field not in self.ignoredFields and field != '' and row[field] != '':
                data[field] = row[field]

        if 'Class/Codec' in row and row['Class/Codec'] != '':
            templateType = data['type']
            if templateType == 'built_in':
                data['codec'] = row['Class/Codec']
            elif templateType == 'simple':
                data['class'] = row['Class/Codec']
            else:
                raise ValueError('Unknown template type: ' + templateType)

        properties = BlockPropertiesReader.read(row, self.pack)
        if len(properties) > 0:
            self.properties[templateId] = properties
            data['properties'] = properties

        if 'Tags' in row and row['Tags'] != '':
            tags = [ResourceLocation(tag.strip()) for tag in row['Tags'].split(',')]
            self.tags[templateId] = tags

        if templateId.namespace != 'minecraft':
            self.writeFile(templateId, data)
