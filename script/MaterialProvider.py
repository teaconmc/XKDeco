import yaml

from Pack import Pack
from Identifier import Identifier
from TableDataProvider import TableDataProvider


class MaterialProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/material', 'materials')
        self.tagTransformers = {}

    def generateRow(self, row, tableConfig):
        materialId = self.pack.defaultIdentifier(row['ID'])
        data = {}
        if materialId in self.tagTransformers:
            transformers = self.tagTransformers[materialId]
        else:
            transformers = {}

        self.field(data, 'DestroyTime', float)
        self.field(data, 'ExplosionResistance', float)
        self.field(data, 'SoundType', str)
        self.field(data, 'MapColor', str)
        self.field(data, 'Instrument', str)
        self.field(data, 'RequiresCorrectTool', lambda v: True if v.lower() == 'true' else None)
        if 'ToolType' in row and row['ToolType'] != '':
            if '' in transformers:
                tags = transformers['']
            else:
                transformers[''] = tags = []
            for tool in row['ToolType'].split(','):
                tags.append(Identifier('mineable/' + tool))
        if 'ToolLevel' in row and row['ToolLevel'] != '':
            if '' in transformers:
                tags = transformers['']
            else:
                transformers[''] = tags = []
            tags.append(Identifier('needs_' + row['ToolLevel'] + '_tool'))
        self.field(data, 'IgnitedByLava', lambda v: True if v.lower() == 'true' else None)
        self.field(data, 'IgniteOdds', lambda v: int(float(v)))
        self.field(data, 'BurnOdds', lambda v: int(float(v)))
        if 'TagTransformers' in row and row['TagTransformers'] != '':
            parsed = yaml.safe_load('{' + row['TagTransformers'] + '}')
            # Map<TagKey, List<TagKey>>
            for key, value in parsed.items():
                if type(value) is list:
                    transformers[key] = [Identifier(v) for v in value]
                elif type(value) is str:
                    transformers[key] = [Identifier(value)]
                else:
                    raise Exception('Invalid tag transformer value: ' + value)

        if len(transformers) > 0:
            self.tagTransformers[materialId] = transformers

        self.writeFile(materialId, data)
