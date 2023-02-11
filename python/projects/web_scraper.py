import requests
from bs4 import BeautifulSoup


def validate_url(input_url):
    ret_val = ""
    if 'www.imdb.com/title/' in input_url:
        ret_val = input_url
    return ret_val


url = validate_url(input("Input the URL:"))
if url == "":
    print("Invalid movie page!")
else:
    response = requests.get(url, headers={'Accept-Language': 'en-US,en;q=0.5'})
    soup = BeautifulSoup(response.content, 'html.parser')
    title = soup.find('h1').text
    desc = soup.find('span', {'data-testid': 'plot-l'}).text
    if title is None or desc is None:
        print("Invalid movie page!")
    else:
        movie_details = {'title': title, 'description': desc}
        print(movie_details)
