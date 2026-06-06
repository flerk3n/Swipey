"""Static seed data: showcase niches, curated trend snippets, and ~20 ideas.

These let the full swipe loop work end-to-end with NO Gemini key. They are
inserted once, the first time the database is empty.
"""
from typing import Dict, List

from . import repository
from .config import settings

# --- Showcase niches ---------------------------------------------------------

NICHES: List[Dict[str, str]] = [
    {
        "id": "tech_reviews",
        "label": "Tech Reviews",
        "description": "A phone reviewer: unboxings, camera shootouts, battery tests, "
        "upgrade advice, budget vs flagship comparisons.",
    },
    {
        "id": "gaming",
        "label": "Gaming",
        "description": "A streamer: game reviews, first-impression live streams, speedrun "
        "breakdowns, settings/optimization guides, indie spotlights.",
    },
    {
        "id": "fashion",
        "label": "Fashion",
        "description": "A fashion influencer: GRWM routines, shopping hauls, trend guides, "
        "outfit-of-the-day picks, styling tips, and seasonal lookbooks.",
    },
]

# --- Verified device facts (official source only) ----------------------------
# Source: https://www.iqoo.com/in/products/param/iqoo15 (official iQOO spec page).
# Only specs confirmed on the official site. Injected into generation/echo prompts
# so the model never invents iQOO 15 specifications.

DEVICE_FACTS: Dict[str, str] = {
    "iQOO 15": (
        "Chipset: Qualcomm Snapdragon 8 Elite Gen 5. "
        "RAM: 12GB or 16GB. Storage: 256GB or 512GB. "
        "Display: 6.85-inch AMOLED, 3168x1440 resolution. "
        "Battery: 7000mAh, 100W wired FlashCharge plus 40W wireless charging. "
        "Rear cameras: triple 50MP — Sony IMX921 main (f/1.88), 50MP ultra-wide "
        "(f/2.05), and 50MP Sony IMX882 3x periscope (f/2.65). Front camera: 32MP. "
        "Software: OriginOS 6 based on Android 16. "
        "Other: ultrasonic fingerprint sensor, Wi-Fi 7, Bluetooth 6.0, USB Type-C, "
        "NFC, 5G. Editions: Legend, Alpha, Apex (about 216-221g)."
    ),
}

DEVICE_FACTS_SOURCE = "https://www.iqoo.com/in/products/param/iqoo15"


def device_facts_block() -> str:
    """Formatted, source-attributed facts block for prompt grounding (all devices)."""
    return device_facts_block_for(" ".join(DEVICE_FACTS.keys()))


def device_facts_block_for(text: str) -> str:
    """Facts block containing ONLY devices actually referenced in ``text``.

    Prevents unrelated content (e.g. a fashion idea) from having a phone spec sheet in its
    prompt, which would otherwise nudge the model to mention the device.
    """
    if not text:
        return ""
    haystack = text.lower()
    relevant = {}
    for name, facts in DEVICE_FACTS.items():
        # Match on any meaningful word of the device name (e.g. "iqoo").
        words = [w for w in name.lower().replace("-", " ").split() if len(w) > 2]
        if any(w in haystack for w in words):
            relevant[name] = facts
    if not relevant:
        return ""
    lines = [f"- {name}: {facts}" for name, facts in relevant.items()]
    return (
        "VERIFIED DEVICE FACTS (official source: "
        + DEVICE_FACTS_SOURCE
        + "):\n"
        + "\n".join(lines)
    )


# --- Curated trend snippets (Phase 5 'real scraping' replaces this) ----------
TRENDS: Dict[str, List[str]] = {
    "tech_reviews": [
        "the iQOO 15 launch — flagship Snapdragon, big battery, gaming focus",
        "new flagship phone launch with a periscope camera",
        "mid-range phones now ship 5000mAh silicon-carbon batteries",
        "USB-C accessory ecosystem after the EU mandate",
        "on-device AI photo editing features",
        "budget phones closing the gap on flagship displays",
    ],
    "gaming": [
        "the iQOO 15 positioned as a mobile-gaming flagship (high refresh, cooling)",
        "indie roguelike breakout hit on Steam",
        "GPU price drop ahead of a new generation",
        "a speedrun world record just fell",
        "handheld PC gaming devices gaining traction",
        "a major studio's surprise shadow-drop release",
    ],
    "fashion": [
        "quiet luxury and 'old money' aesthetic trending",
        "the return of low-rise and Y2K denim",
        "viral capsule-wardrobe minimalism",
        "thrift-flip and second-hand styling on the rise",
        "a seasonal color palette dropping for the new collections",
    ],
}


# --- Seed ideas --------------------------------------------------------------

def _idea(content_type, title, description, tokens, tags):
    return {
        "content_type": content_type,
        "title": title,
        "description": description,
        "summary_tokens": tokens,
        "tags": tags,
    }


TECH_IDEAS: List[Dict] = [
    _idea(
        "unboxing",
        "Unboxing the iQOO 15: gaming flagship",
        "Cold-open on the seal breaking, then call out the Snapdragon 8 Elite Gen 5 and "
        "the 6.85-inch AMOLED. Lead with the gaming-phone angle and the first boot.",
        "iqoo,15,unboxing,gaming,flagship,snapdragon,amoled,display,reveal,boot",
        ["#iQOO15", "#Unboxing", "#GamingPhone"],
    ),
    _idea(
        "review",
        "Camera shootout: $400 vs $1200",
        "Shoot the same 6 scenes on a budget phone and a flagship, blind-rank them on "
        "screen, and reveal prices last. Let the cheap phone win at least once.",
        "camera,shootout,budget,flagship,photo,comparison,blind,test,price,night",
        ["#Camera", "#Comparison", "#Budget"],
    ),
    _idea(
        "review",
        "iQOO 15 battery + performance: 7-day test",
        "Endurance diary on the iQOO 15: daily screen-on time from the 7000mAh battery, "
        "thermals under load, and whether the Snapdragon 8 Elite Gen 5 holds up for heavy use.",
        "iqoo,15,battery,7000mah,performance,thermals,snapdragon,screen,test,load",
        ["#iQOO15", "#Battery", "#Performance"],
    ),
    _idea(
        "short_form",
        "Should you upgrade? The 2-year rule",
        "A 45-second framework: compare your current phone's camera, battery health, and "
        "update support against the new model before you spend a cent.",
        "upgrade,phone,two,year,rule,camera,battery,health,updates,decision",
        ["#Upgrade", "#Advice", "#Shorts"],
    ),
    _idea(
        "long_form",
        "The truth about on-device AI photo editing",
        "Test the magic-eraser style tools on real messy photos. Show the failures, not "
        "just the demo shots, and explain what runs locally vs in the cloud.",
        "ai,photo,editing,on,device,eraser,local,cloud,test,failures",
        ["#AI", "#Photography", "#DeepDive"],
    ),
    _idea(
        "review",
        "Best USB-C accessories after the EU mandate",
        "Round up cables, hubs, and chargers that actually hit the speeds they claim. "
        "Include one cheap pick that punches above its price.",
        "usbc,accessory,cable,hub,charger,eu,mandate,speed,roundup,value",
        ["#Accessories", "#USBC", "#Roundup"],
    ),
    _idea(
        "short_form",
        "3 phone settings you should change today",
        "Punchy list: refresh-rate, adaptive battery, and the privacy toggle most people "
        "never find. One tip per beat, fast cuts.",
        "settings,phone,refresh,rate,battery,privacy,tips,change,today,quick",
        ["#Tips", "#Settings", "#Shorts"],
    ),
    _idea(
        "long_form",
        "Why mid-range phones feel flagship now",
        "Break down the trickle-down of displays, chipsets, and cameras. Argue the price "
        "of 'good enough' has quietly collapsed.",
        "midrange,flagship,display,chipset,camera,price,value,trickle,down,collapse",
        ["#MidRange", "#Analysis", "#Value"],
    ),
    _idea(
        "review",
        "30 days with a foldable as my only phone",
        "Living-with-it review: the crease at month one, pocket comfort, app continuity, "
        "and whether the novelty survives a real routine.",
        "foldable,review,month,crease,pocket,app,continuity,daily,driver,durability",
        ["#Foldable", "#Review", "#LongTerm"],
    ),
    _idea(
        "short_form",
        "The accessory that fixed my phone's worst flaw",
        "Tease the flaw first, then reveal a sub-$20 fix. End on a before/after that makes "
        "the upgrade obvious.",
        "accessory,fix,flaw,cheap,upgrade,before,after,reveal,phone,case",
        ["#Accessories", "#Hack", "#Shorts"],
    ),
]

GAMING_IDEAS: List[Dict] = [
    _idea(
        "review",
        "This indie roguelike ate my whole weekend",
        "Open mid-run at a near-death moment, then rewind to explain the loop. Sell the "
        "'one more run' feeling without spoiling the unlocks.",
        "indie,roguelike,review,run,loop,unlock,weekend,addictive,steam,gameplay",
        ["#Indie", "#Roguelike", "#Review"],
    ),
    _idea(
        "live_stream",
        "First impressions: blind playthrough live",
        "Go in with zero trailers watched. React in real time, take chat's bets on the "
        "first plot twist, and rate it after the opening hour.",
        "first,impressions,blind,playthrough,live,react,chat,twist,opening,rating",
        ["#LiveStream", "#FirstLook", "#Reaction"],
    ),
    _idea(
        "long_form",
        "How a speedrun record actually falls",
        "Frame-by-frame breakdown of the trick that saved 4 seconds. Explain the risk vs "
        "reward so a non-speedrunner gets why it's huge.",
        "speedrun,record,frame,trick,seconds,risk,reward,breakdown,world,strategy",
        ["#Speedrun", "#Breakdown", "#Esports"],
    ),
    _idea(
        "short_form",
        "Best settings for max FPS (no quality loss)",
        "Five toggles that buy frames without making the game look worse. Show the FPS "
        "counter jump on each change.",
        "settings,fps,optimization,frames,quality,toggle,performance,guide,boost,pc",
        ["#FPS", "#Optimization", "#Guide"],
    ),
    _idea(
        "review",
        "Indie spotlight: the game no one's covering",
        "Champion an under-1000-review gem. Lead with the single mechanic that makes it "
        "special and a call to wishlist it.",
        "indie,spotlight,hidden,gem,mechanic,wishlist,underrated,steam,discovery,review",
        ["#Indie", "#Spotlight", "#HiddenGem"],
    ),
    _idea(
        "live_stream",
        "Beating the hardest boss on stream tonight",
        "Set a clear stakes goal, track attempt count on an overlay, and let chat suggest "
        "one strategy per death. Payoff is the clear.",
        "boss,hardest,stream,attempts,overlay,chat,strategy,death,clear,challenge",
        ["#LiveStream", "#Challenge", "#Boss"],
    ),
    _idea(
        "long_form",
        "iQOO 15 mobile gaming test: can a phone beat a handheld?",
        "Push the iQOO 15's Snapdragon 8 Elite Gen 5 with the heaviest mobile titles: "
        "sustained FPS, thermals, and touch latency on the 6.85-inch AMOLED. Verdict on "
        "whether it replaces a dedicated handheld.",
        "iqoo,15,mobile,gaming,snapdragon,fps,thermals,latency,handheld,amoled",
        ["#iQOO15", "#MobileGaming", "#Performance"],
    ),
    _idea(
        "short_form",
        "Ranking every starter from worst to best",
        "Fast, opinionated tier list. Bait the comments by putting the fan-favorite low "
        "and defending it in one line.",
        "ranking,tier,list,starter,worst,best,opinion,bait,comments,defend",
        ["#TierList", "#Ranking", "#Shorts"],
    ),
    _idea(
        "review",
        "The shadow-drop everyone's sleeping on",
        "Surprise-release review. Explain why no marketing hurt it, then make the case it "
        "deserves a second look this week.",
        "shadow,drop,surprise,release,review,marketing,sleeper,case,week,underrated",
        ["#ShadowDrop", "#Review", "#Sleeper"],
    ),
    _idea(
        "live_stream",
        "Co-op chaos: carrying random teammates",
        "Clip-bait the funniest fails, narrate the comeback, and end on the clutch. Build "
        "the episode around one impossible win.",
        "coop,chaos,random,teammates,fails,comeback,clutch,clip,win,multiplayer",
        ["#CoOp", "#Clips", "#Multiplayer"],
    ),
]

FASHION_IDEAS: List[Dict] = [
    _idea(
        "grwm",
        "GRWM for a last-minute dinner date",
        "Talk through the outfit decision in real time: the base piece, the one risky "
        "accessory, and the shoe swap that pulls it together. Keep it conversational.",
        "grwm,outfit,dinner,date,accessory,shoes,styling,getready,look,decision",
        ["#GRWM", "#OOTD", "#DateNight"],
    ),
    _idea(
        "haul",
        "Affordable haul: everything under $40",
        "Try-on haul of budget finds. Rate each piece on fit, fabric, and how many outfits "
        "it unlocks. Flag the one return and the one repeat-buy.",
        "haul,affordable,budget,tryon,fit,fabric,outfit,return,review,shopping",
        ["#Haul", "#Affordable", "#TryOn"],
    ),
    _idea(
        "long_form",
        "The quiet luxury trend, decoded",
        "Break down what actually makes a look read as 'quiet luxury' — fabric, fit, and "
        "neutral palette — then recreate it on a normal budget.",
        "quiet,luxury,trend,fabric,fit,neutral,palette,budget,recreate,styling",
        ["#QuietLuxury", "#TrendGuide", "#Style"],
    ),
    _idea(
        "post",
        "Outfit of the day: one blazer, three ways",
        "Style a single blazer for work, weekend, and night out. Show the swap pieces and "
        "explain why each combo changes the vibe.",
        "ootd,blazer,three,ways,work,weekend,night,swap,versatile,styling",
        ["#OOTD", "#Styling", "#Versatile"],
    ),
    _idea(
        "haul",
        "Trying the viral Y2K denim everyone's posting",
        "Honest try-on of the low-rise revival. Who it flatters, how to size it, and the "
        "tops that balance the proportion.",
        "y2k,denim,lowrise,tryon,fit,size,proportion,tops,trend,honest",
        ["#Y2K", "#Denim", "#TryOn"],
    ),
    _idea(
        "long_form",
        "Build a 10-piece capsule wardrobe",
        "Pick 10 pieces that make 20+ outfits. Lay out the formula — neutrals, one statement, "
        "layering — and show the grid of combinations.",
        "capsule,wardrobe,ten,pieces,outfits,neutrals,statement,layering,formula,minimal",
        ["#Capsule", "#Minimalist", "#Wardrobe"],
    ),
    _idea(
        "grwm",
        "Get ready with me: thrifted-only outfit",
        "Style a full look using only second-hand finds. Narrate the thrift-flip — what you "
        "hemmed, tucked, or belted to make it look intentional.",
        "grwm,thrift,secondhand,flip,hem,tuck,belt,sustainable,outfit,styling",
        ["#Thrifted", "#GRWM", "#Sustainable"],
    ),
    _idea(
        "post",
        "5 ways to instantly elevate a basic outfit",
        "Quick list: tuck, cuff, layer, accessorize, tailor. Show a plain jeans-and-tee "
        "before/after for each tweak.",
        "elevate,basic,outfit,tuck,cuff,layer,accessorize,tailor,before,after",
        ["#StyleTips", "#OOTD", "#Basics"],
    ),
    _idea(
        "long_form",
        "Fall trend guide: what to actually buy",
        "Round up the season's key trends, then cut it to the 4 worth your money and the "
        "ones to skip. Pair each with a piece you already own.",
        "fall,trend,guide,season,buy,skip,key,pieces,pairing,shopping",
        ["#TrendGuide", "#Fall", "#Shopping"],
    ),
    _idea(
        "haul",
        "Designer vs dupe: is it worth it?",
        "Side-by-side of a designer piece and its high-street dupe. Compare fabric, stitching, "
        "and feel, then call whether the splurge is justified.",
        "designer,dupe,compare,fabric,stitching,feel,splurge,worth,sidebyside,review",
        ["#DesignerVsDupe", "#Haul", "#Honest"],
    ),
]


def seed_if_empty() -> None:
    """Insert seed ideas once, only when the ideas table is empty."""
    if repository.count_ideas() > 0:
        return

    user = settings.default_user_id
    batches = [
        ("tech_reviews", TECH_IDEAS),
        ("gaming", GAMING_IDEAS),
        ("fashion", FASHION_IDEAS),
    ]
    for niche, ideas in batches:
        for item in ideas:
            repository.insert_idea(
                user_id=user,
                niche=niche,
                content_type=item["content_type"],
                title=item["title"],
                description=item["description"],
                summary_tokens=item["summary_tokens"],
                tags=item["tags"],
                source="seed",
                status="pending",
            )
