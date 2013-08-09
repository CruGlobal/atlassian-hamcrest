atlassian-hamcrest
==================

Library containing useful Hamcrest Matchers that can match deep object graphs.

For motivation, please read http://blogs.atlassian.com/2009/06/how_hamcrest_can_save_your_sou.

This project was forked from https://labs.atlassian.com/svn/AHAM/trunk rev 3110.  The original project seems 
to have died.


The main changes made since the fork involved fixing bugs that allow the library to usefully work with Lists,
Maps, and Sets.  The original library did not handle these well, as it only looked at non-transient array fields.
