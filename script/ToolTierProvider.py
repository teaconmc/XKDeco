import yaml

from Pack import Pack
from Identifier import Identifier
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

        self.writeFile(self.pack.defaultIdentifier(row['ID']), data)

    def convertToIngredient(self, value: str):
        if value.startswith('#'):
            return {
                'tag': Identifier(value[1:])
            }
        else:
            return {
                'item': Identifier(value)
            }
