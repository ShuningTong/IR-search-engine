# IR-search-engine

##V1 how to run:
Import the folder into eclipse and run SearchIndex.java. Figure out the build path if necessary.
Currently only support single word search, and no rankings yet.

##V2 how to run:
The same with that of V1.

##V2 new feature:
1. Support search by skill keyword, years of experience, and address (address actually does not affect search results yet) as well as content.
2. Support sort by relevance or post date.
3. Return 20 top docs for every search.

##V2 problems:
1. Still, some results' urls have been expired.
2. If you see error related with something like LUCENE_NUM, just clear the index and run BuildIndex.java to rebuild the index. I haven't figured out why we have to rebuild index sometimes.

##V3 how to run:
The same with that of V1.

##V2 new feature:
1. Address does affect search result now.
2. Add C#, C++, CSS to skillSet. Now we have 10 skills in total.
3. Add snippets of description to each search result.
4. Skill and Content query terms will appear and be highlighted (be surrounded with <B></B> tabs) in the snippets of description.
But "C#" and "C++" will not appear, because highlighters depend on term vectors which are analyzed using StandardAnalyzer.
5. Use WhiteSpaceAnalyzer instead of StandardAnalyzer for SKILL and ADDRESS fields, so that Lucene does not treat "OR", "IN" (State abbreviations) as stop words.
6. Add another field for postDate, so that postDate can be presented in their original format like 3/4/16.
7. SKILL and ADDRESS are both "MUST" match fields now. So some queries will return less results.

##V4 how to run:
The same with that of V1.

##V4 new feature:
1. Experience is a "MUST" field now, the only optional field is additional information.
2. Replace some null companyNames with "Unknown".
3. Delete some results that have expired.

