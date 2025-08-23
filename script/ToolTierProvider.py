import yaml

from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class ToolTierProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/tool_tier', 'tool_tiers')

    def generateRow(self, row, tableConfig):
        data = {}

        self.field(data, 'IncorrectBlocksForDrops', str)
        self.field(data, 'Uses', lambda v: int(float(v)))
        self.field(data, 'Speed', float)
        self.field(data, 'AttackDamageBonus', float)
        self.field(data, 'EnchantmentValue', lambda v: int(float(v)))
        self.field(data, 'RepairIngredient', self.convertToIngredient)

        self.writeFile(self.pack.defaultResourceLocation(row['ID']), data)

    def convertToIngredient(self, value: str):
        if value.startswith('#'):
            return {
                'tag': ResourceLocation(value[1:])
            }
        else:
            return {
                'item': ResourceLocation(value)
            }
