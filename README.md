Moonlight
=========
[![Build Status](https://travis-ci.org/cab404/moonlight.svg?branch=dev)](https://travis-ci.org/cab404/moonlight)

Just a little easy-to-use framework for writing interaction libraries, e.g for Android clients.

### Usage
Works like this: you add some modules using `Module`, bind modules in `Page`-s to keys, add some requests for dealing with js and your brand new lib pretty much done! 
(Perfect for site developers, who have no time dealing with APIs, or just lazy)

### How does it works?
After you call `fetch` from your page, it will run throught `bindParsers` and then start new loading thread (still, `fetch` invokation is blocking, but if anyone 
needs I can add nonblocking variant).

All data from loading thread will be passed to HTML parsing thread. It'll fix simple errors, e.g not closed `<input>` tags
and then send HTML blocks to your modules. 

If any of them likes (yup, I actually created `doYouLikeIt` method :) ) given block, it will be passed to
`extractData` method. You can here parse HTML to your object using my simple implementation of XPath (it is actually not, but still very useful) and other simple utilities.
If your module requested block, but in `extractData` it SUDDENLY realized it actually isn't block it was looking for (e.g you was searching data in script tags, and
accidently found some other js code), just return null - it won't reach handlers.

And what's next? Just wait your objects in `handle` method in your page, and do whatever you want to do with them!

### Example
So, let's try fetching LIVESTREET_SECURITY_KEY.
First of all, we will need module to parse it:

```java

import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.framework.ModuleImpl;
import com.cab404.moonlight.parser.HTMLTree;
import com.cab404.moonlight.parser.Tag;
import com.cab404.moonlight.util.SU;

public class LSKeyModule extends ModuleImpl<String> {

    @Override public boolean doYouLikeIt(Tag tag) {
// We will need to check all the script tags.
        return "script".equals(tag.name);
    }
    
    @Override public String extractData(HTMLTree block, AccessProfile profile) {
// Get all data from block starting with first tag 
        String js = block.getContents(0); 
        
// Let's check if we need this block.
        if (!js.contains("LIVESTREET_SECURITY_KEY")) return null;
        
// Okay, we have a key there.
// Let's mark this module as finished, so page will stop loading.
// (because we have only this parser, page will stop loading if no working parsers left)
        finish();

// And extract it using some no-regex functions, written manually in pure Java.
        return SU.sub(
                js,
                "LIVESTREET_SECURITY_KEY = '",
                "'"
        );
    }

}

```

Сreate page and use it!
```java
public static void main(String... args){

        Page page = new Page() {

            @Override protected String getURL() {
                return "/";
            }
            @Override protected void bindParsers(ModularBlockParser base) {
                base.bind(new LSKeyModule(), 0);
            }
            @Override public void handle(Object object, int key) {
                System.out.println("Our key is " + object);
            }
        };

        AccessProfile profile = new AccessProfile("tabun.everypony.ru");

        page.fetch(profile);
}
```

`AccessProfile` will store cookies and host address, and you can actually serialize it with `serialize()` and then restore with `AccessProfile.parseString()`.

### Used in
This project was forked from my [libtabun](https://github.com/cab404/libtabun), as I found it pretty useful in many cases. So it is used in several phone apps and bots.

