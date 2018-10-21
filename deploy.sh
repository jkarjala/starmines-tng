TARGET=jpkware@jpkware.com:public_html/smtng
pscp target/index.html $TARGET/
pscp target/scala-2.12/starmines-the-next-generation-*opt.* $TARGET/
pscp target/scala-2.12/classes/* $TARGET/classes
pscp -r target/scala-2.12/classes/lib $TARGET/classes
pscp -r target/scala-2.12/classes/res $TARGET/classes
