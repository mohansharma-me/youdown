Youtube video downloader application for low-end J2ME devices. Allow users to search/play/download videos from application easily.

I developed this J2ME application while doing my diploma in computer engineering for fun. Actually at that time (around 2009) android smartphone not much famous than j2me-based nokia device (specifically in India).

There was not a single application which allow users to do this. Idea to develop such project came after i learned J2ME Mobile Application Development through library book. My hostel mates wanted me to develop such application as many of them not having laptop.

I developed this project without using any youtube related api (frankly speaking i was not aware of API web service technology at that time), so how i did it ? very simple. I programed application to fetch html content from search result page and extracted required video details using string searching and manipulation and displayed using List component with little thumbnails and when user selects particular video program again fetchs html content of that video from video uri and display more details and also extracted and displayed RTSP player on screen and when user chose to download video option then i used fetch html content from video uri but with desktop user agent and somehow extracts video direct uri and put it in downloading queue.

I never felt that my first ever fun release will hit nearly 4 lakh downloads. After such huge response i developed another version of it with cool UI changes which look-likes Opera Mini.

Application Link : http://gallery.mobile9.com/f/2152146/

I uploaded source code on github at http://github.com/mohansharma-me/youdown (feel free to use it)

Portfolio link : http://mohansharma.me/portfolio/mobile-development/youdown

Technologies used : J2ME (Java 2 Mobile Edition), YouTube Scrapping, RTSP Streaming, Canvas GUI (Custom GUI)

Screenshots :

![](https://i2.wp.com/mohansharma.me/wp-content/uploads/2016/06/YouDown-Video-Details.jpg?w=229&h=306&crop)
![](https://i1.wp.com/mohansharma.me/wp-content/uploads/2016/06/YouDown-Download.jpg?w=229&h=306&crop)
![](https://i2.wp.com/mohansharma.me/wp-content/uploads/2016/06/YouDown-Search.jpg?w=230&h=306&crop)

Copyright © 2011 mohansharma.me
