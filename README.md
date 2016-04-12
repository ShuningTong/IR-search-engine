# IR-search-engine

V1 how to run:
Import the folder into eclipse and run SearchIndex.java. Figure out the build path if necessary.
Currently only support single word search, and no rankings yet.

V2 how to run:
The same with that of V1.

V2 new feature:
1. Support search by skill keyword, years of experience, and address (address actually does not affect search results yet) as well as content.
2. Support sort by relevance or post date.
3. Return 20 top docs for every search.

V2 problems:
1. Still, some results' urls have been expired.
2. If you see error related with something like LUCENE_NUM, just clear the index and run BuildIndex.java to rebuild the index. I haven't figured out why we have to rebuild index sometimes.


