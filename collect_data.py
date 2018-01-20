# references
# For twitter Get/search API : https://dev.twitter.com/rest/reference/get/search/tweets
# for techniques to overcome limitations : http://www.karambelkar.info/2015/01/how-to-use-twitters-search-rest-api-most-effectively./
# tweepy API refernce : http://docs.tweepy.org/en/v3.5.0/api.html?highlight=api.search#API.search
# meaning of each tweeter field : https://dev.twitter.com/overview/api/tweets#obj-coordinates
# JSON conversions used in the program : https://docs.python.org/2/library/json.html
# twitter api rate limits: https://dev.twitter.com/rest/public/rate-limiting

###### All the imports
import sys
import jsonpickle
import os
import tweepy
import json
import time 
from tweepy import OAuthHandler

######## Twitter application client secret keys
consumer_key = 'iUfCa7jjBrbQswDasqhqiN3DS'
consumer_secret = 'Hobl4LFvZ6rJNuCINHE72ACTlROgdecHciQ0N0wzBEPp4ACABI'
access_token = '2573201196-qmwoqS7uCj1rI1Oc95ParmiAZHHVc31K3FFPWya'
access_secret = '57edsiAEyTLgh3SJGZ7OptiB4LIN9D1MSgUT8DDWkXanp'

############ Configurable fields 
topic = "DeMonetisation"
search_terms = "demonetisationdisaster"

max_tweets = 100
max_tweets_per_query = 10
max_id = -1
last_tweet_created_at_date = None

### Summary of tweets
tweet_count = 0
original_tweet_count = 0
retweet_count = 0

auth = OAuthHandler(consumer_key, consumer_secret)
#auth.set_access_token(access_token, access_secret)

#do this to get result in JSON format. Search for it and you will get the stackoverflow link 
api = tweepy.API(auth, parser=tweepy.parsers.JSONParser())

# 1.Each search API limits max result to 100 records and 2. application limit is 180 request per 15 mins
# To overcome point 1 above we are using while loop
request_count = 1
print("[")
while original_tweet_count < max_tweets:
    # print("iteration ",request_count)
    # print("max_id ",max_id)
    if max_id < 0:
        results = api.search(q=search_terms, count=max_tweets_per_query)
    else:
        results = api.search(q=search_terms, count=max_tweets_per_query,max_id=max_id-1)
    
    jsonobj = json.dumps(results["statuses"])
    for tweet in results["statuses"]:
        #print(json.dumps(tweet))
        tweet_count+=1
        jsonstr = json.dumps(tweet)
        jsonobj = json.loads(jsonstr)
        if jsonobj.get('retweeted_status'):
            #print("******* Retweeted status ******* ")
            retweet_count = retweet_count + 1       
        else:
            #print("****** Original Tweet *******")
            jsonobj['topic'] = topic
            original_tweet_count+= 1
            print (json.dumps(jsonobj))
            print(",")
            #print (jsonobj['id'])
            max_id = jsonobj['id']
            last_tweet_created_at_date = jsonobj['created_at']

    request_count += 1
    if request_count >= 180:
        request_count = 1
        #print("Resetting request_count")
        time.sleep(900)            

    #print(json.loads(json.dumps(results["statuses"][-1]))['id'])
    #max_id = json.loads( json.dumps(results["statuses"][0])).get('id')
    #last_tweet_created_at_date = json.loads(json.dumps(results["statuses"][-1]))['created_at']

print("]")
print("Max Id for ",topic," is ",max_id)
print("last tweet created at date",last_tweet_created_at_date)    
print("Total count = "  ,tweet_count)
print("Retweet Count = " ,retweet_count)
print("original Tweet Count = " ,original_tweet_count)