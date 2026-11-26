import { Composition } from "remotion";
import { KodoxAd, KODOX_AD_DURATION } from "./KodoxAd";

export const Root: React.FC = () => {
  return (
    <>
      <Composition
        id="KodoxAd"
        component={KodoxAd}
        durationInFrames={KODOX_AD_DURATION}
        fps={30}
        width={1920}
        height={1080}
      />
      <Composition
        id="KodoxAdVertical"
        component={KodoxAd}
        durationInFrames={KODOX_AD_DURATION}
        fps={30}
        width={1080}
        height={1920}
      />
    </>
  );
};
