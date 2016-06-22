atlassian-hamcrest
==================

Library containing useful Hamcrest Matchers that can match deep object graphs.

For motivation, please read http://blogs.atlassian.com/2009/06/how_hamcrest_can_save_your_sou and 
http://blogs.atlassian.com/2009/06/hamcrest_saves_your_soul_now_w.

This project was forked from https://labs.atlassian.com/svn/AHAM/trunk rev 3110.  The original project seems 
to have died.  Also, the original svn repo (and all of labs.atlassian.com) is unavailable now.


The main changes made since the fork involved fixing bugs that allow the library to usefully work with Lists,
Maps, and Sets.  The original library did not handle these well, as it only looked at non-transient array fields.


license
-------
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
