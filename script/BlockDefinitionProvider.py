import titlecase
import yaml

import BlockPropertiesReader
from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class BlockDefinitionProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/block', 'blocks')
        self.templateTags = None
        self.templateProperties = None
        self.tagTransformers = None
        self.glassTypes = None
        self.order = []
        self.blocks = set()

    def generate(self):
        self.templateTags = self.pack.providers['block_templates'].tags
        self.templateProperties = self.pack.providers['block_templates'].properties
        self.tagTransformers = self.pack.providers['materials'].tagTransformers
        if 'glass_types' in self.pack.providers:
            self.glassTypes = self.pack.providers['glass_types'].glassTypes
        # for key, value in self.templateTags.items():
        #     print(str(key), value)
        super().generate()
        self.pack.providers['metadata'].putRegistryOrder('block', self.order)

    def generateRow(self, row, csvConfig):
        self.order.append(row['ID'])
        blockId = self.pack.defaultResourceLocation(row['ID'])
        self.blocks.add(blockId)
        data = {}
        tags = set()
        templateId = None
        if 'Template' in row and row['Template'] != 'block' and row['Template'] != '':
            index = row['Template'].find('{')
            if index > 0:
                templateId = row['Template'][:index]
                template = {
                    'kiwi:type': templateId
                }
                for key, value in yaml.safe_load(row['Template'][index:]).items():
                    template[key] = value
                data['template'] = template
            else:
                templateId = row['Template']
                data['template'] = templateId
            templateId = ResourceLocation(templateId)
            if templateId in self.templateTags:
                tags.update(self.templateTags[templateId])

        properties = BlockPropertiesReader.read(row, self.pack)
        data.update(properties)
        if templateId is not None and templateId in self.templateProperties:
            components = None
            if 'components' in properties and 'components' in self.templateProperties[templateId]:
                components = self.templateProperties[templateId]['components'] + properties['components']
            properties.update(self.templateProperties[templateId])
            if components is not None:
                properties['components'] = components

        if 'SustainsPlant' in row and row['SustainsPlant'].lower() == 'true':
            self.pack.providers['block_tags'].addBlock(ResourceLocation('kiwi:sustains_plant'), blockId)
        # item = {}
        # if 'ItemGroup' in row and row['ItemGroup'] != '':
        #     item['tab'] = row['ItemGroup']
        # if len(item) > 0:
        #     data['item'] = item
        if 'ItemGroup' in row and row['ItemGroup'] != '':
            self.pack.providers['creative_tabs'].addContent(row['ItemGroup'], blockId)
        if 'MainFamily' in row and row['MainFamily'] != '':
            self.pack.providers['block_families'].addBlock(self.pack.defaultResourceLocation(row['MainFamily']), blockId)
        translationKey = 'block.{namespace}.{name}'.format(namespace=self.pack.config['namespace'], name=row['ID'])
        if 'Name:en_us' in row and row['Name:en_us'] != '':
            self.pack.providers['translations'].putTranslation('en_us', translationKey, row['Name:en_us'])
        else:
            self.pack.providers['translations'].putTranslation('en_us', translationKey, titlecase.titlecase(blockId.path.replace('_', ' ')))
        if 'SecondaryName' in csvConfig:
            fieldName = 'Name:' + csvConfig['SecondaryName']
            if fieldName in row and row[fieldName] != '':
                self.pack.providers['translations'].putTranslation(csvConfig['SecondaryName'], translationKey, row[fieldName])

        if self.glassTypes is not None and 'glass_type' in properties:
            glassType = None
            if ResourceLocation(properties['glass_type']) in self.glassTypes:
                glassType = self.glassTypes[ResourceLocation(properties['glass_type'])]
            if glassType is None or 'skip_rendering' not in glassType or glassType['skip_rendering']:
                self.pack.providers['block_tags'].addEntry(ResourceLocation('impermeable'), blockId)

        materialId = self.pack.defaultResourceLocation(properties['material']) if 'material' in properties else None
        if materialId in self.tagTransformers:
            for key, value in self.tagTransformers[materialId].items():
                if key == '':
                    tags.update(value)
                    continue
                key = ResourceLocation(key)
                if key in tags:
                    tags.remove(key)
                    tags.update(value)

        for tag in tags:
            self.pack.providers['block_tags'].addEntry(tag, blockId)

        self.writeFile(blockId, data)
