import json
import yaml

slots = {
    'target': '@xkdeco:roof',
    'slots': [
        {
            'when': [
                {
                    'shape': 'straight',
                    'facing': 'north',
                    'variant': 'normal'
                },
                {
                    'shape': 'straight',
                    'facing': 'north',
                    'variant': 'slow',
                    'half': 'upper'
                }
            ],
            'tag': ['@shape', '@variant', '@half', '@facing'],
            'sides': {
                'north': {
                    'tag': ['*roof:front']
                }
            }
        }
    ]
}
# print(json.dumps(slots, indent='\t'))
# print(yaml.dump(slots, indent=2, sort_keys=False))

# choices = {
#     'target': '@xkdeco:roof',
#     'fallback': {}
# }

link = {
    'from': 'roof:front',
    'to': 'roof:back',
    'interest': 1,
    'test_tag': [
        {
            'key': 'facing',
            'operator': 'is_opposite'
        }
    ]
}
print(yaml.dump(link, indent=2, sort_keys=False))

link = {
    'from': 'roof:side',
    'to': 'roof:side',
    'test_tag': [
        'facing',
        'variant',
        'half'
    ]
}
print(yaml.dump(link, indent=2, sort_keys=False))