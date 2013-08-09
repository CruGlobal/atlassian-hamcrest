atlassian-hamcrest
==================

Library containing useful Hamcrest Matchers that can match deep object graphs.

For motivation, please read http://blogs.atlassian.com/2009/06/how_hamcrest_can_save_your_sou and 
http://blogs.atlassian.com/2009/06/hamcrest_saves_your_soul_now_w.

This project was forked from https://labs.atlassian.com/svn/AHAM/trunk rev 3110.  The original project seems 
to have died.  Also, the original svn repo (and all of labs.atlassian.com) is unavailable now.


The main changes made since the fork involved fixing bugs that allow the library to usefully work with Lists,
Maps, and Sets.  The original library did not handle these well, as it only looked at non-transient array fields.
