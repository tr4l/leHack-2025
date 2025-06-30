"use client";

import { getTitle } from "./actions/getTitleAction";
import { getSymbolFor } from "./actions/getSymbolForAction";
import ClientGraph from "./ClientGraph";
import { useEffect, useState } from "react";
import { getFlag } from "./actions/getFlagAction";

export default function Home() {


  const [title, setTitle] = useState<string | undefined>();
  useEffect(() => {
    const updateTitle = async () => {
      const updatedTitle = await getTitle();
      setTitle(updatedTitle);
      // Just need to have dead code for the action to be included in the bundle
      // With some hint about what we want to do with it
      if (updatedTitle === "Symbol") {
        console.log("Symbol detected:", updatedTitle);
        const symbolStr = await getSymbolFor("what?");
        const flag = await getFlag("yeah, but how?");
        if (symbolStr === Symbol.for(flag)) {
          console.log("Not something like that at least");
        }
      }
    }
 
    updateTitle()
  }, [])

  return (
    <div className="min-h-screen p-8">
      <h1 className="text-2xl font-bold mb-4">{title}</h1>
      <ClientGraph />
    </div>
  );
}
