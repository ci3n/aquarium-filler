import java.util.*;

/**
 * Created by ci3n on 03/30/16.
 */
public class AquariumFiller {
    private int[] aquarium = null;
    private int[] water = null;
    private int waterAmount = -1;

    /**
     * Sets an array which defines aquarium
     *
     * @param aquarium int array
     */
    public void setAquarium(final int[] aquarium) {
        this.aquarium = aquarium;
        this.water = null;
        this.waterAmount = -1;
    }

    private boolean isInitialised() {
        return aquarium != null;
    }

    /**
     * @return amount of water that can be stored in the tank
     */
    public int getWaterAmount() {
        if (!isInitialised()) throw new IllegalStateException("Aquarium not setted");
        if (waterAmount != -1) return waterAmount;
        waterAmount = 0;
        int[] water = getWater();
        for (int i = 0; i < water.length; i++) {
            waterAmount += water[i];
        }
        return waterAmount;
    }

    /**
     * @return int array that contains amount of water in each column
     */
    public int[] getWater() {
        if (!isInitialised()) throw new IllegalStateException("Aquarium not setted");
        if (water != null) return water;

        water = new int[aquarium.length];
        int[][] aquariums = getSeparateAquariums();
        int pos = 0;
        for (int i = 0; i < aquariums.length; i++) {
            if (aquariums[i].length != 0) {
                int[] tmp = getWaterForSingleAquarium(aquariums[i], i);
                System.arraycopy(tmp, 0, water, pos, tmp.length);
                pos += tmp.length;
            }
            pos++;
        }
        return water;
    }

    private int[] getWaterForSingleAquarium(final int[] singleAquarium, final int aquariumNumber) {
        int[] water = new int[singleAquarium.length];
        List<Section> sections = calculateSections(singleAquarium);
        Section lastSection = sections.remove(sections.size() - 1);
        sections.addAll(sections.size(), splitLastSection(lastSection, aquariumNumber));
//        System.err.println("Sections" + sections);
        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            int maxHeight = Math.min(singleAquarium[currentSection.start], singleAquarium[currentSection.end]);
            for (int pos = currentSection.start; pos < currentSection.end; pos++) {
                water[pos] = Math.max(0, maxHeight - singleAquarium[pos]);
            }
        }
//        System.err.println("Water " + Arrays.toString(water));
        return water;
    }

    /**
     * Splits initial aquariums into parts without zeroes
     * @return array of separate aquariums
     */
    private int[][] getSeparateAquariums() {
        List<Integer> zeroes = getZeroes();
        zeroes.add(aquarium.length);
        int[][] result = new int[zeroes.size()][];
        int i = 0;
        int predPosition = 0;
        for (Integer pos : zeroes) {
            result[i] = new int[pos - predPosition];
            System.arraycopy(aquarium, predPosition, result[i], 0, result[i].length);
            predPosition = pos + 1;
            i++;
        }
        return result;
    }

    /**
     * Reverses a section and tries to split it then reverses result back again
     * @param section
     * @param aquariumIndex index of aquarium to which section belongs
     * @return list of new sections
     */
    private List<Section> splitLastSection(final Section section, final int aquariumIndex) {
        int[] tmp = new int[section.end - section.start + 1];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = aquarium[section.end - i + aquariumIndex];
        }
        List<Section> tmpList = calculateSections(tmp);
        for (Section newSection : tmpList) {
            int i = newSection.start;
            newSection.start = section.end - newSection.end;
            newSection.end = section.end - i;
        }
        return tmpList;
    }

    /**
     * Calculates sections in current aquarium
     * New section starts when next column is higher than (or equals to)
     * the highest column in current section
     * @param aquarium aquarium to be divided in sections
     * @return list of sections
     */
    private List<Section> calculateSections(final int[] aquarium) {
        int[] interestingHeights = new int[aquarium.length];

        for (Integer i : getLocalMaximums(aquarium)) {
            interestingHeights[i] = aquarium[i];
        }

        Deque<Section> sectionStack = new ArrayDeque<>();
        sectionStack.push(new Section(0));
        for (int i = 1; i < interestingHeights.length; i++) {
            Section currentSection = sectionStack.peek();
            int currentHeight = interestingHeights[i];

            if (currentHeight >= interestingHeights[currentSection.start]) {
                currentSection.end = i;
                sectionStack.push(new Section(i));
            }
        }

        sectionStack.peek().end = Math.max(interestingHeights.length - 1, 0);
        List<Section> result = new ArrayList<>();
        Iterator<Section> sectionIterator = sectionStack.descendingIterator();
        while (sectionIterator.hasNext()) {
            result.add(sectionIterator.next());
        }
        return result;
    }

    /**
     * Finds positions of zeroes
     */
    private List<Integer> getZeroes() {
        List<Integer> zeroes = new ArrayList<>();
        for (int i = 0; i < aquarium.length; i++) {
            if (aquarium[i] == 0) zeroes.add(i);
        }
        return zeroes;
    }

    /**
     * Finds local maximums in an array
     * Local maximum is an element which is bigger than
     * all it's non-equal neighbours
     * @param aquarium initial array
     * @return list of maximums' indexes
     */
    private List<Integer> getLocalMaximums(final int[] aquarium) {
        List<Integer> localMax = new ArrayList<>();
        int[] tmp = new int[aquarium.length + 2];
        System.arraycopy(aquarium, 0, tmp, 1, aquarium.length);
        int currentMax = 0;
        int currentPosition = 0;
        boolean rise = false;
        for (int i = 1; i < tmp.length; i++) {
            if (tmp[i] > currentMax) {
                currentMax = tmp[i];
                currentPosition = i;
                rise = true;
            } else if (tmp[i] < currentMax) {
                if (rise) localMax.add(currentPosition - 1);
                currentPosition = i;
                currentMax = tmp[i];
                rise = false;
            }
        }
        return localMax;
    }

    private class Section {
        int start;
        int end;

        Section(final int start) {
            this.start = start;
        }

        @Override
        public String toString() {
            return "[" + start + "," + end + "]";
        }
    }
}
