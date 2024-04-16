import requests


def downloadFileFromGoogleSheets(file_id, dest):
    url = f'https://docs.google.com/spreadsheets/d/{file_id}/export?format=xlsx'
    print('Downloading', url, 'to', dest)
    r = requests.get(url)
    r.raise_for_status()
    with open(dest, 'wb') as f:
        f.write(r.content)

# if __name__ == '__main__':
#     downloadFileFromGoogleSheets('18y2gvjkuD6iutqg4evWvteOJQkhJBoST6FQk96kFNx4', 'test.xlsx')