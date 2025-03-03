# Migration Plan for Streeek: Android Native to Kotlin Multiplatform with Compose Multiplatform

## **Objective**
To migrate the Streeek app from an Android Native implementation to a Kotlin Multiplatform (KMP) framework with Compose Multiplatform for the user interface. This transition will enable code sharing across Android and iOS platforms, reduce long-term maintenance overhead, and improve feature parity across platforms.

## **Deadline**
The migration project is **_targeted_** for completion by **`31st March 2025`**.

---

## **Why Migrate to KMP and Compose Multiplatform?**

### **1. Code Reusability**
- Sharing business logic across Android and iOS will reduce duplication and improve development efficiency.
- Tools like `Ktor` for networking and `Room` for databases allow seamless integration across platforms.

### **2. Modern and Flexible UI**
- Compose Multiplatform provides a declarative approach to building UI, enabling reuse of UI components.

### **3. Improved Scalability**
- KMP allows platform-specific implementation only where necessary, reducing the complexity of managing separate codebases.

### **4. Time Efficiency**
- Faster feature delivery due to shared logic and unified UI development.
- Reduced long-term maintenance costs.

---

## **Strategy Overview**

### **1. Incremental Migration**
The migration will follow an incremental approach to ensure that the app remains functional throughout the process. This minimizes risks and allows for frequent testing and validation of changes.

### **2. Modular Architecture**
Adopt a modular architecture to facilitate separation of concerns and reusability. The key modules are:
- **sources**: Contains platform-independent business logic for fetching data from either network or local cache.
- **data**: Contains platform-independent data handling.
- **resources**: Contains all resources for the app (fonts, images, e.t.c).
- **ui**: Implements Compose Multiplatform Material theme and UI components.
- **feature**: Encapsulate individual app features.

### **3. Comprehensive Testing**
Ensure thorough testing at **EVERY** stage:
- Unit tests for shared logic.
- UI tests for Compose components.
- Integration testing for feature completeness (maybe).

---

## **Detailed Migration Plan**

### **Week 1: Sources & Data (1st - 8th March 2025)**
#### **Tasks**
1. **Create `sources` module**
   - Migrate `remote` sources to shared module using `ktor`
   - Migrate `local` sources to shared module using `room`
   - Refactor app to use the new sources module
   
2. **Create `data` Module**:
    - Migrate repository & their implementations to new data module.
    - Refactor app to use the shared data module.

3. **Platform-Specific Implementations**:
    - Use `expect`/`actual` for platform-specific APIs (e.g., file storage, HTTP client configurations).

4. **Testing**:
    - Write unit tests for the sources & data layer.

#### **Outcome**
The app’s data layer is fully functional in the shared module and tested for both Android and iOS.

---

### **Weeks 2: Resources, UI, and Initial Features (9th - 15th March 2025)**
#### **Tasks**
1. **Resources Module**:
    - Consolidate all app resources (strings, fonts, audio, images) into a single shared module.

2. **UI Module**:
    - Set up Compose Multiplatform dependencies.
    - Migrate themes and typography to shared module.
    - Start migrating common UI components to Compose Multiplatform (e.g., LevelComponent).

3. **Feature Module**:
    - Migrate individual feature modules for core features like:
      - contributions
      - leaderboards
      - teams
      - levels
    - Refactor feature logic to use shared data and UI modules.

4. **Testing**:
    - Implement unit tests for features and UI components.

#### **Outcome**
All features are refactored and functional in the shared and feature-specific module, with a Compose Multiplatform UI.

---

### **Week 3: Remaining Features (16th - 22nd March 2025)**
#### **Tasks**
1. **Feature Module (Remaining)**:
   - Complete the migration of remaining feature modules, including:
      - Gamified GitHub contributions.
      - Analytics and progress tracking.
   - Ensure feature logic is fully integrated with shared data and UI modules.

2. **UI Enhancements**:
   - Refine and finalize common UI components.
   - Address any platform-specific UI challenges.

3. **Testing**:
   - Write unit and integration tests for the remaining features and enhanced UI components.

#### **Outcome**
All features are fully refactored and functional in the shared and feature-specific modules, with a Compose Multiplatform UI implemented.

---

### **Week 4: Full Integration, Internal Testing & Update Continuous Delivery (23rd - 29th March 2025)**
#### **Tasks**
1. **Integration Testing**:
    - Test the integration between the shared module, UI module, and feature module.
    - Resolve any platform-specific issues identified during testing.

2. **Performance Optimization**:
    - Profile the app to identify and resolve bottlenecks in shared logic and UI rendering.

3. **Internal Testing**:
    - Conduct end-to-end testing with internal users to validate app stability.

4. **Update CI/CD**:
    - update current git actions for deployment to both platforms.

#### **Outcome**
A fully functional app with shared business logic and Compose Multiplatform UI, tested and optimized for both platforms.

---

### **Week 5: Shipping (31st March 2025)**
#### **Tasks**
1. Prepare app for release:
    - Generate APK and IPA builds for Android and iOS.
    - Perform final QA checks.

2. Publish:
    - Deploy the updated app to Google Play Store and Apple App Store.

#### **Outcome**
The Streeek app is successfully migrated and released as a Kotlin Multiplatform app with Compose Multiplatform UI.

---

## **Risk Management**
- **Risk**: Platform-specific issues during migration.
    - **Mitigation**: Use `expect/actual` declarations and extensive testing.
- **Risk**: Delays in migration.
    - **Mitigation**: Follow the incremental approach and prioritize critical features.
- **Risk**: Performance bottlenecks.
    - **Mitigation**: Use profiling tools and optimize shared code.

---

## **Tools and Dependencies**
- **Kotlin Multiplatform**: Shared business logic.
- **Compose Multiplatform**: Declarative UI framework.
- **Ktor**: Networking.
- **Room**: Database management.
- **Coroutines**: Asynchronous programming.
- **Gradle**: Build system.

---

## **Conclusion**
This migration plan outlines a clear path to transition Streeek from Android Native to Kotlin Multiplatform and Compose Multiplatform, ensuring the app’s functionality and user experience are preserved while enabling code sharing and maintainability.
