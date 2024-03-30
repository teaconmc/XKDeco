import yaml

from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class MaterialProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/material', 'materials')
        self.tagTransformers = {}

    def generateRow(self, row, csvConfig):
        if row['ID'] == '':
            return
        data = {}
        if 'DestroyTime' in row and row['DestroyTime'] != '':
            data['destroy_time'] = float(row['DestroyTime'])
        if 'ExplosionResistance' in row and row['ExplosionResistance'] != '':
            data['explosion_resistance'] = float(row['ExplosionResistance'])
        if 'SoundType' in row and row['SoundType'] != '':
            data['sound_type'] = row['SoundType']
        if 'MapColor' in row and row['MapColor'] != '':
            data['map_color'] = row['MapColor']
        if 'Instrument' in row and row['Instrument'] != '':
            data['instrument'] = row['Instrument']
        if 'RequiresCorrectTool' in row and row['RequiresCorrectTool'].lower() == 'true':
            data['requires_correct_tool'] = True
        if 'IgnitedByLava' in row and row['IgnitedByLava'].lower() == 'true':
            data['ignited_by_lava'] = True
        if 'IgniteOdds' in row and row['IgniteOdds'] != '':
            data['ignite_odds'] = int(row['IgniteOdds'])
        if 'BurnOdds' in row and row['BurnOdds'] != '':
            data['burn_odds'] = int(row['BurnOdds'])
        if 'TagTransformers' in row and row['TagTransformers'] != '':
            transformers = yaml.safe_load('{' + row['TagTransformers'] + '}')
            # Map<TagKey, List<TagKey>>
            m = {}
            for key, value in transformers.items():
                if type(value) is list:
                    m[key] = [ResourceLocation(v) for v in value]
                elif type(value) is str:
                    m[key] = [ResourceLocation(value)]
                else:
                    raise Exception('Invalid tag transformer value: ' + value)
            self.tagTransformers[self.pack.defaultResourceLocation(row['ID'])] = m

        self.writeJson(self.pack.defaultResourceLocation(row['ID']), data)
